package com.example.backend.service;

import com.example.backend.model.RentalContract;
import com.example.backend.model.RentalRequest;
import com.example.backend.model.Customer;
import com.example.backend.model.Automobile;
import com.example.backend.model.enums.RequestStatus;
import com.example.backend.repository.RentalContractRepository;
import com.example.backend.repository.RentalRequestRepository;
import com.example.backend.repository.AutomobileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.Optional;

@Service
@Transactional
public class RentalContractService {

    @Autowired
    private RentalContractRepository contractRepository;

    @Autowired
    private RentalRequestRepository requestRepository;

    @Autowired
    private AutomobileRepository automobileRepository;

    public List<RentalContract> findAll() {
        return contractRepository.findAll();
    }

    public Optional<RentalContract> findById(String id) {
        return contractRepository.findById(id);
    }

    public List<RentalContract> findByCustomer(Customer customer) {
        return contractRepository.findByRentalRequestCustomer(customer);
    }

    public List<RentalContract> findActiveContracts() {
        return contractRepository.findActiveContracts();
    }

    public List<RentalContract> findExpiringContracts(int daysAhead) {
        return contractRepository.findExpiringContracts(daysAhead);
    }

    @Transactional
    public RentalContract createFromApprovedRequest(String requestId) {
        RentalRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado"));

        if (request.getStatus() != RequestStatus.APPROVED) {
            throw new IllegalStateException("Apenas pedidos aprovados podem gerar contratos");
        }

        // Verificar se já existe contrato para este pedido
        if (contractRepository.existsByRentalRequest(request)) {
            throw new IllegalStateException("Já existe um contrato para este pedido");
        }

        RentalContract contract = new RentalContract();
        contract.setId(UUID.randomUUID().toString());
        contract.setRentalRequest(request);
        contract.setStartDate(request.getDesiredStartDate());
        contract.setEndDate(request.getDesiredEndDate());
        contract.setValue(request.getEstimatedValue());
        contract.setSigningDate(LocalDate.now());
        contract.setStatus("ATIVO");

        // Gerar termos do contrato
        contract.setTerms(generateContractTerms(request));

        // Marcar pedido como executado
        request.setStatus(RequestStatus.EXECUTED);
        requestRepository.save(request);

        // Marcar automóvel como indisponível
        Automobile automobile = request.getAutomobile();
        automobile.setAvailable(false);
        automobileRepository.save(automobile);

        return contractRepository.save(contract);
    }

    @Transactional
    public RentalContract renewContract(String contractId, LocalDate newEndDate) {
        RentalContract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new IllegalArgumentException("Contrato não encontrado"));

        if (newEndDate.isBefore(contract.getEndDate())) {
            throw new IllegalArgumentException("Nova data de fim deve ser posterior à atual");
        }

        // Calcular novo valor baseado na extensão
        long additionalDays = ChronoUnit.DAYS.between(contract.getEndDate(), newEndDate);
        Automobile automobile = contract.getRentalRequest().getAutomobile();
        double additionalValue = additionalDays * automobile.getDailyRate();

        contract.setEndDate(newEndDate);
        contract.setValue(contract.getValue() + additionalValue);
        contract.setRenewalCount((contract.getRenewalCount() != null ? contract.getRenewalCount() : 0) + 1);
        contract.setStatus("RENOVADO");

        // Atualizar termos com informações de renovação
        String renewalTerms = "\n\n--- RENOVAÇÃO DO CONTRATO ---\n";
        renewalTerms += "Data da renovação: " + LocalDate.now() + "\n";
        renewalTerms += "Nova data de término: " + newEndDate + "\n";
        renewalTerms += "Dias adicionais: " + additionalDays + "\n";
        renewalTerms += "Valor adicional: R$ " + String.format("%.2f", additionalValue);

        contract.setTerms(contract.getTerms() + renewalTerms);

        return contractRepository.save(contract);
    }

    @Transactional
    public void finalizeContract(String contractId) {
        RentalContract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new IllegalArgumentException("Contrato não encontrado"));

        // Finalizar contrato usando o método da entidade
        contract.finalizeContract();

        // Adicionar informações de finalização aos termos
        String finalizationTerms = "\n\n--- FINALIZAÇÃO DO CONTRATO ---\n";
        finalizationTerms += "Data de finalização: " + LocalDate.now() + "\n";
        finalizationTerms += "Contrato finalizado com sucesso.";

        contract.setTerms(contract.getTerms() + finalizationTerms);

        // Salvar as alterações
        contractRepository.save(contract);
    }

    public boolean isContractActive(String contractId) {
        Optional<RentalContract> contract = contractRepository.findById(contractId);
        return contract.map(RentalContract::isCurrentlyActive).orElse(false);
    }

    public double calculateTotalContractValue(String contractId) {
        RentalContract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new IllegalArgumentException("Contrato não encontrado"));
        return contract.calculateTotalValue();
    }

    private String generateContractTerms(RentalRequest request) {
        StringBuilder terms = new StringBuilder();

        terms.append("CONTRATO DE ALUGUEL DE VEÍCULO\n\n");
        terms.append("CONTRATANTE: ").append(request.getCustomer().getName()).append("\n");
        terms.append("CPF: ").append(request.getCustomer().getCpf()).append("\n");
        terms.append("ENDEREÇO: ").append(request.getCustomer().getAddress()).append("\n\n");

        terms.append("VEÍCULO ALUGADO:\n");
        terms.append("Marca: ").append(request.getAutomobile().getBrand()).append("\n");
        terms.append("Modelo: ").append(request.getAutomobile().getModel()).append("\n");
        terms.append("Ano: ").append(request.getAutomobile().getYear()).append("\n");
        terms.append("Placa: ").append(request.getAutomobile().getLicensePlate()).append("\n\n");

        terms.append("PERÍODO DO ALUGUEL:\n");
        terms.append("Data de início: ").append(request.getDesiredStartDate()).append("\n");
        terms.append("Data de término: ").append(request.getDesiredEndDate()).append("\n");

        long days = ChronoUnit.DAYS.between(request.getDesiredStartDate(), request.getDesiredEndDate());
        terms.append("Total de dias: ").append(days).append("\n\n");

        terms.append("VALORES:\n");
        terms.append("Diária: R$ ").append(String.format("%.2f", request.getAutomobile().getDailyRate())).append("\n");
        terms.append("Valor total: R$ ").append(String.format("%.2f", request.getEstimatedValue())).append("\n\n");

        terms.append("TERMOS E CONDIÇÕES:\n");
        terms.append("1. O veículo deve ser devolvido nas mesmas condições em que foi entregue.\n");
        terms.append("2. O contratante é responsável por qualquer dano causado ao veículo.\n");
        terms.append("3. O pagamento deve ser efetuado conforme acordado.\n");
        terms.append("4. O contrato pode ser renovado mediante acordo entre as partes.\n");

        if (request.getObservations() != null && !request.getObservations().trim().isEmpty()) {
            terms.append("\nOBSERVAÇÕES ESPECIAIS:\n");
            terms.append(request.getObservations()).append("\n");
        }

        terms.append("\nData de assinatura: ").append(LocalDate.now());

        return terms.toString();
    }

    public void delete(String id) {
        contractRepository.deleteById(id);
    }
}