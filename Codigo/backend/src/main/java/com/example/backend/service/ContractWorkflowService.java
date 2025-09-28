package com.example.backend.service;

import com.example.backend.model.*;
import com.example.backend.model.enums.RequestStatus;
import com.example.backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class ContractWorkflowService {

    @Autowired
    private RentalRequestRepository rentalRequestRepository;

    @Autowired
    private RentalContractRepository rentalContractRepository;

    @Autowired
    private CreditContractRepository creditContractRepository;

    @Autowired
    private AutomobileRepository automobileRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private BankRepository bankRepository;

    /**
     * Fluxo completo: Pedido → Análise → Aprovação → Contrato
     */
    @Transactional
    public RentalContract processRentalRequestToContract(String requestId, String agentId) {
        // 1. Buscar o pedido
        RentalRequest request = rentalRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado"));

        if (request.getStatus() != RequestStatus.CREATED) {
            throw new IllegalStateException("Pedido deve estar em status CREATED para ser processado");
        }

        // 2. Análise do pedido
        analyzeRentalRequest(request);

        // 3. Aprovação automática baseada em critérios
        if (shouldApproveRequest(request)) {
            request.setStatus(RequestStatus.APPROVED);
            rentalRequestRepository.save(request);

            // 4. Criar contrato de aluguel
            return createRentalContractFromRequest(request);
        } else {
            request.setStatus(RequestStatus.REJECTED);
            rentalRequestRepository.save(request);
            throw new IllegalStateException("Pedido rejeitado na análise automática");
        }
    }

    /**
     * Fluxo completo com crédito: Pedido → Análise → Crédito → Contrato
     */
    @Transactional
    public RentalContract processRentalRequestWithCredit(String requestId, String bankId,
                                                         double interestRate, int termInMonths) {
        // 1. Processar pedido normal
        RentalRequest request = rentalRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado"));

        if (request.getStatus() != RequestStatus.CREATED) {
            throw new IllegalStateException("Pedido deve estar em status CREATED");
        }

        // 2. Análise de crédito primeiro
        if (!analyzeCreditworthiness(request.getCustomer(), request.getEstimatedValue())) {
            request.setStatus(RequestStatus.REJECTED);
            rentalRequestRepository.save(request);
            throw new IllegalStateException("Cliente não aprovado para crédito");
        }

        // 3. Criar contrato de crédito
        CreditContract creditContract = createCreditContract(request, bankId, interestRate, termInMonths);

        // 4. Aprovar o pedido
        request.setStatus(RequestStatus.APPROVED);
        rentalRequestRepository.save(request);

        // 5. Criar contrato de aluguel
        RentalContract rentalContract = createRentalContractFromRequest(request);

        return rentalContract;
    }

    /**
     * Finaliza um contrato e libera o automóvel
     */
    @Transactional
    public void finalizeRentalContract(String contractId, String finalizationReason) {
        RentalContract contract = rentalContractRepository.findById(contractId)
                .orElseThrow(() -> new IllegalArgumentException("Contrato não encontrado"));

        // Marcar contrato como finalizado (usando método correto)
        contract.finalizeContract();

        // Adicionar observações sobre a finalização
        String finalizationNote = "Contrato finalizado em " + LocalDate.now() +
                " - Motivo: " + finalizationReason;
        contract.setTerms(contract.getTerms() + "\n\n" + finalizationNote);

        // Marcar automóvel como disponível
        if (contract.getRentalRequest() != null && contract.getRentalRequest().getAutomobile() != null) {
            Automobile automobile = contract.getRentalRequest().getAutomobile();
            automobile.setAvailable(true);
            automobileRepository.save(automobile);
        }

        rentalContractRepository.save(contract);
    }

    /**
     * Renovação automática de contratos próximos ao vencimento
     */
    @Transactional
    public List<RentalContract> processContractRenewals() {
        List<RentalContract> expiringContracts = rentalContractRepository.findExpiringContracts(7);

        return expiringContracts.stream()
                .filter(this::shouldAutoRenew)
                .map(this::autoRenewContract)
                .toList();
    }

    /**
     * Processo de liquidação antecipada de crédito
     */
    @Transactional
    public void processEarlyLiquidation(String creditId, double liquidationAmount) {
        CreditContract credit = creditContractRepository.findById(creditId)
                .orElseThrow(() -> new IllegalArgumentException("Contrato de crédito não encontrado"));

        if (!"ATIVO".equals(credit.getStatus())) {
            throw new IllegalStateException("Apenas créditos ativos podem ser liquidados");
        }

        // Calcular valor de liquidação (com possível desconto)
        double remainingValue = calculateRemainingDebt(credit);

        if (liquidationAmount >= remainingValue) {
            credit.setStatus("LIQUIDADO");
            credit.setLiquidationDate(LocalDate.now());
            credit.setObservations("Liquidação antecipada realizada em " + LocalDate.now());
            creditContractRepository.save(credit);
        } else {
            throw new IllegalArgumentException("Valor insuficiente para liquidação. Necessário: " +
                    String.format("%.2f", remainingValue));
        }
    }

    /**
     * Avalia se um pedido deve ser aprovado automaticamente
     */
    private boolean shouldApproveRequest(RentalRequest request) {
        // Critérios para aprovação automática

        // 1. Automóvel deve estar disponível
        if (request.getAutomobile() == null || !request.getAutomobile().isAvailable()) {
            return false;
        }

        // 2. Valor estimado deve estar dentro do limite
        if (request.getEstimatedValue() == null || request.getEstimatedValue() > 50000) {
            return false;
        }

        // 3. Cliente deve ter dados básicos
        Customer customer = request.getCustomer();
        if (customer == null || customer.getCpf() == null || customer.getName() == null) {
            return false;
        }

        // 4. Período deve ser válido
        if (!request.validateDates()) {
            return false;
        }

        // 5. Cliente não deve ter mais de 3 contratos ativos
        List<RentalContract> activeContracts = rentalContractRepository.findByRentalRequestCustomer(customer);
        long activeCount = activeContracts.stream()
                .filter(contract -> "ATIVO".equals(contract.getStatus()) || "RENOVADO".equals(contract.getStatus()))
                .count();

        return activeCount < 3;
    }

    /**
     * Analisa um pedido de aluguel
     */
    private void analyzeRentalRequest(RentalRequest request) {
        // Calcular valor estimado se não foi calculado
        if (request.getEstimatedValue() == null) {
            double calculatedValue = request.calculateValue();
            request.setEstimatedValue(calculatedValue);
        }

        // Marcar como sob análise
        request.setStatus(RequestStatus.UNDER_ANALYSIS);
        rentalRequestRepository.save(request);
    }

    /**
     * Análise de capacidade creditícia
     */
    private boolean analyzeCreditworthiness(Customer customer, Double requestedAmount) {
        if (customer == null || requestedAmount == null) return false;

        // Verificar créditos ativos existentes
        List<CreditContract> existingCredits = creditContractRepository.findByRentalRequestCustomer(customer);

        long activeCredits = existingCredits.stream()
                .filter(credit -> "ATIVO".equals(credit.getStatus()))
                .count();

        // Limite de 3 créditos ativos
        if (activeCredits >= 3) return false;

        // Verificar valor total de crédito
        double totalActiveCredit = existingCredits.stream()
                .filter(credit -> "ATIVO".equals(credit.getStatus()))
                .mapToDouble(CreditContract::getValue)
                .sum();

        // Limite total de 200.000
        return (totalActiveCredit + requestedAmount) <= 200000.0;
    }

    /**
     * Cria contrato de aluguel a partir de um pedido aprovado
     */
    private RentalContract createRentalContractFromRequest(RentalRequest request) {
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
        rentalRequestRepository.save(request);

        // Marcar automóvel como indisponível
        Automobile automobile = request.getAutomobile();
        automobile.setAvailable(false);
        automobileRepository.save(automobile);

        return rentalContractRepository.save(contract);
    }

    /**
     * Cria contrato de crédito
     */
    private CreditContract createCreditContract(RentalRequest request, String bankId,
                                                double interestRate, int termInMonths) {
        Bank bank = bankRepository.findById(bankId)
                .orElseThrow(() -> new IllegalArgumentException("Banco não encontrado"));

        CreditContract creditContract = new CreditContract();
        creditContract.setId(UUID.randomUUID().toString());
        creditContract.setRentalRequest(request);
        creditContract.setValue(request.getEstimatedValue());
        creditContract.setInterestRate(interestRate);
        creditContract.setTerm(termInMonths);
        creditContract.setGrantDate(LocalDate.now());
        creditContract.setStatus("ATIVO");
        creditContract.setGrantingBank(bank);

        return creditContractRepository.save(creditContract);
    }

    /**
     * Verifica se um contrato deve ser renovado automaticamente
     */
    private boolean shouldAutoRenew(RentalContract contract) {
        // Lógica para renovação automática (pode ser baseada em preferências do cliente)
        return contract.getRenewalCount() != null && contract.getRenewalCount() < 2;
    }

    /**
     * Renova automaticamente um contrato
     */
    private RentalContract autoRenewContract(RentalContract contract) {
        LocalDate newEndDate = contract.getEndDate().plusMonths(1);
        contract.renew(newEndDate);
        return rentalContractRepository.save(contract);
    }

    /**
     * Calcula dívida restante de um crédito
     */
    private double calculateRemainingDebt(CreditContract credit) {
        if (credit.getGrantDate() == null || credit.getTerm() == null) return credit.getValue();

        long monthsElapsed = java.time.temporal.ChronoUnit.MONTHS.between(
                credit.getGrantDate(), LocalDate.now());

        if (monthsElapsed >= credit.getTerm()) return 0.0;

        int remainingMonths = credit.getTerm() - (int) monthsElapsed;
        return credit.calculateInstallment() * remainingMonths;
    }

    /**
     * Gera termos do contrato
     */
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

        long days = java.time.temporal.ChronoUnit.DAYS.between(
                request.getDesiredStartDate(), request.getDesiredEndDate());
        terms.append("Total de dias: ").append(days).append("\n\n");

        terms.append("VALORES:\n");
        terms.append("Diária: R$ ").append(String.format("%.2f", request.getAutomobile().getDailyRate())).append("\n");
        terms.append("Valor total: R$ ").append(String.format("%.2f", request.getEstimatedValue())).append("\n\n");

        terms.append("TERMOS E CONDIÇÕES:\n");
        terms.append("1. O veículo deve ser devolvido nas mesmas condições em que foi entregue.\n");
        terms.append("2. O contratante é responsável por qualquer dano causado ao veículo.\n");
        terms.append("3. O pagamento deve ser efetuado conforme acordado.\n");
        terms.append("4. O contrato pode ser renovado mediante acordo entre as partes.\n\n");

        if (request.getObservations() != null && !request.getObservations().trim().isEmpty()) {
            terms.append("OBSERVAÇÕES ESPECIAIS:\n");
            terms.append(request.getObservations()).append("\n\n");
        }

        terms.append("Data de assinatura: ").append(LocalDate.now());

        return terms.toString();
    }
}