package com.example.backend.service;

import com.example.backend.dto.CreditContractCreateDTO;
import com.example.backend.dto.CreditContractResponseDTO;
import com.example.backend.dto.CreditContractUpdateDTO;
import com.example.backend.dto.CustomerSummaryDTO;
import com.example.backend.model.BankAgent;
import com.example.backend.model.CreditContract;
import com.example.backend.model.Customer;
import com.example.backend.repository.BankAgentRepository;
import com.example.backend.repository.CreditContractRepository;
import com.example.backend.repository.CustomerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CreditContractService {

    private static final Logger logger = LoggerFactory.getLogger(CreditContractService.class);

    private final CreditContractRepository creditContractRepository;
    private final CustomerRepository customerRepository;
    private final BankAgentRepository bankAgentRepository;

    public CreditContractService(CreditContractRepository creditContractRepository,
                                 CustomerRepository customerRepository,
                                 BankAgentRepository bankAgentRepository) {
        this.creditContractRepository = creditContractRepository;
        this.customerRepository = customerRepository;
        this.bankAgentRepository = bankAgentRepository;
    }

    /**
     * Cria ou atualiza um limite de crédito para um cliente
     */
    @Transactional
    public CreditContractResponseDTO createOrUpdateCreditLimit(String bankAgentUsername,
                                                               CreditContractCreateDTO dto) {
        logger.info("Criando/atualizando limite de crédito - BankAgent: {}, Customer: {}, Limit: {}",
                bankAgentUsername, dto.getCustomerId(), dto.getCreditLimit());

        // Buscar o banco agente
        BankAgent bankAgent = bankAgentRepository.findByUsername(bankAgentUsername)
                .orElseThrow(() -> new IllegalArgumentException("Agente bancário não encontrado"));

        // Buscar o cliente
        Customer customer = customerRepository.findById(dto.getCustomerId())
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado"));

        // Verificar se já existe um contrato ativo
        Optional<CreditContract> existingContract = creditContractRepository
                .findByCustomerAndBankAgent(customer.getId(), bankAgent.getId());

        CreditContract contract;

        if (existingContract.isPresent()) {
            // Atualizar contrato existente
            contract = existingContract.get();
            logger.info("Atualizando contrato existente: {}", contract.getId());
            contract.setCreditLimit(dto.getCreditLimit());
        } else {
            // Criar novo contrato
            contract = new CreditContract();
            contract.setId(UUID.randomUUID().toString());
            contract.setCustomer(customer);
            contract.setBankAgent(bankAgent);
            contract.setCreditLimit(dto.getCreditLimit());
            contract.setAvailableLimit(dto.getCreditLimit());
            contract.setStatus("ACTIVE");
            contract.setCreatedAt(LocalDate.now());
            logger.info("Criando novo contrato: {}", contract.getId());
        }

        CreditContract saved = creditContractRepository.save(contract);
        logger.info("Contrato salvo com sucesso: {}", saved.getId());

        return convertToResponseDTO(saved);
    }

    /**
     * Atualiza um contrato de crédito existente
     */
    @Transactional
    public CreditContractResponseDTO updateCreditContract(String contractId,
                                                          String bankAgentUsername,
                                                          CreditContractUpdateDTO dto) {
        logger.info("Atualizando contrato {} - BankAgent: {}", contractId, bankAgentUsername);

        CreditContract contract = creditContractRepository.findById(contractId)
                .orElseThrow(() -> new IllegalArgumentException("Contrato não encontrado"));

        // Verificar se o banco agente é o dono do contrato
        if (!contract.getBankAgent().getUsername().equals(bankAgentUsername)) {
            throw new IllegalArgumentException("Você não tem permissão para modificar este contrato");
        }

        if (dto.getCreditLimit() != null) {
            contract.setCreditLimit(dto.getCreditLimit());
        }

        if (dto.getStatus() != null) {
            contract.setStatus(dto.getStatus());
        }

        CreditContract updated = creditContractRepository.save(contract);
        return convertToResponseDTO(updated);
    }

    /**
     * Lista todos os contratos de crédito gerenciados por um banco
     */
    @Transactional(readOnly = true)
    public List<CreditContractResponseDTO> getAllCreditContractsByBankAgent(String bankAgentUsername) {
        logger.info("Listando contratos do banco: {}", bankAgentUsername);

        List<CreditContract> contracts = creditContractRepository
                .findAllByBankAgentUsername(bankAgentUsername);

        return contracts.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Busca um contrato específico
     */
    @Transactional(readOnly = true)
    public CreditContractResponseDTO getCreditContractById(String contractId, String bankAgentUsername) {
        CreditContract contract = creditContractRepository.findById(contractId)
                .orElseThrow(() -> new IllegalArgumentException("Contrato não encontrado"));

        // Verificar se o banco agente é o dono do contrato
        if (!contract.getBankAgent().getUsername().equals(bankAgentUsername)) {
            throw new IllegalArgumentException("Você não tem permissão para visualizar este contrato");
        }

        return convertToResponseDTO(contract);
    }

    /**
     * Verifica se um cliente tem limite disponível suficiente para um valor específico
     * com um banco específico
     */
    @Transactional(readOnly = true)
    public boolean hasAvailableCredit(String customerUsername, String bankAgentUsername, Double amount) {
        logger.info("Verificando limite disponível - Customer: {}, BankAgent: {}, Amount: {}",
                customerUsername, bankAgentUsername, amount);

        Optional<CreditContract> contract = creditContractRepository
                .findByCustomerUsernameAndBankAgentUsername(customerUsername, bankAgentUsername);

        if (contract.isEmpty()) {
            logger.warn("Nenhum contrato de crédito encontrado entre {} e {}",
                    customerUsername, bankAgentUsername);
            return false;
        }

        CreditContract creditContract = contract.get();

        if (!"ACTIVE".equals(creditContract.getStatus())) {
            logger.warn("Contrato de crédito {} não está ativo", creditContract.getId());
            return false;
        }

        boolean hasLimit = creditContract.hasAvailableLimit(amount);
        logger.info("Cliente {} tem limite disponível: {} (disponível: {}, necessário: {})",
                customerUsername, hasLimit, creditContract.getAvailableLimit(), amount);

        return hasLimit;
    }

    /**
     * Reduz o limite disponível quando um pedido é aprovado
     */
    @Transactional
    public void reduceAvailableLimit(String customerUsername, String bankAgentUsername, Double amount) {
        logger.info("Reduzindo limite disponível - Customer: {}, BankAgent: {}, Amount: {}",
                customerUsername, bankAgentUsername, amount);

        Optional<CreditContract> contractOpt = creditContractRepository
                .findByCustomerUsernameAndBankAgentUsername(customerUsername, bankAgentUsername);

        if (contractOpt.isEmpty()) {
            logger.warn("Contrato não encontrado para redução de limite");
            return;
        }

        CreditContract contract = contractOpt.get();
        contract.reduceAvailableLimit(amount);
        creditContractRepository.save(contract);

        logger.info("Limite reduzido. Novo limite disponível: {}", contract.getAvailableLimit());
    }

    /**
     * Restaura o limite disponível quando um pedido é cancelado/rejeitado
     */
    @Transactional
    public void restoreAvailableLimit(String customerUsername, String bankAgentUsername, Double amount) {
        logger.info("Restaurando limite disponível - Customer: {}, BankAgent: {}, Amount: {}",
                customerUsername, bankAgentUsername, amount);

        Optional<CreditContract> contractOpt = creditContractRepository
                .findByCustomerUsernameAndBankAgentUsername(customerUsername, bankAgentUsername);

        if (contractOpt.isEmpty()) {
            logger.warn("Contrato não encontrado para restauração de limite");
            return;
        }

        CreditContract contract = contractOpt.get();
        contract.restoreAvailableLimit(amount);
        creditContractRepository.save(contract);

        logger.info("Limite restaurado. Novo limite disponível: {}", contract.getAvailableLimit());
    }

    /**
     * Deleta um contrato de crédito
     */
    @Transactional
    public void deleteCreditContract(String contractId, String bankAgentUsername) {
        CreditContract contract = creditContractRepository.findById(contractId)
                .orElseThrow(() -> new IllegalArgumentException("Contrato não encontrado"));

        if (!contract.getBankAgent().getUsername().equals(bankAgentUsername)) {
            throw new IllegalArgumentException("Você não tem permissão para deletar este contrato");
        }

        creditContractRepository.delete(contract);
        logger.info("Contrato {} deletado com sucesso", contractId);
    }

    /**
     * Converte entidade para DTO
     */
    private CreditContractResponseDTO convertToResponseDTO(CreditContract contract) {
        CreditContractResponseDTO dto = new CreditContractResponseDTO();
        dto.setId(contract.getId());
        dto.setBankAgentId(contract.getBankAgent().getId());
        dto.setBankAgentUsername(contract.getBankAgent().getUsername());
        dto.setCreditLimit(contract.getCreditLimit());
        dto.setAvailableLimit(contract.getAvailableLimit());
        dto.setCreatedAt(contract.getCreatedAt());
        dto.setUpdatedAt(contract.getUpdatedAt());
        dto.setStatus(contract.getStatus());

        // Calcular limite usado
        Double usedLimit = contract.getCreditLimit() - contract.getAvailableLimit();
        dto.setUsedLimit(usedLimit);

        // Calcular percentual de uso
        if (contract.getCreditLimit() != null && contract.getCreditLimit() > 0) {
            Double percentage = (usedLimit / contract.getCreditLimit()) * 100;
            dto.setUsagePercentage(Math.round(percentage * 100.0) / 100.0);
        } else {
            dto.setUsagePercentage(0.0);
        }

        // Informações do cliente
        if (contract.getCustomer() != null) {
            CustomerSummaryDTO customerDTO = new CustomerSummaryDTO();
            customerDTO.setId(contract.getCustomer().getId());
            customerDTO.setUsername(contract.getCustomer().getUsername());
            customerDTO.setEmail(contract.getCustomer().getEmail());
            dto.setCustomer(customerDTO);
        }

        return dto;
    }
}