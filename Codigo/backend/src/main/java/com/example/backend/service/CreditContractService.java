package com.example.backend.service;

import com.example.backend.model.CreditContract;
import com.example.backend.model.RentalRequest;
import com.example.backend.model.Bank;
import com.example.backend.model.Customer;
import com.example.backend.repository.CreditContractRepository;
import com.example.backend.repository.RentalRequestRepository;
import com.example.backend.repository.BankRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.Optional;

@Service
@Transactional
public class CreditContractService {

    @Autowired
    private CreditContractRepository creditRepository;

    @Autowired
    private RentalRequestRepository requestRepository;

    @Autowired
    private BankRepository bankRepository;

    public List<CreditContract> findAll() {
        return creditRepository.findAll();
    }

    public Optional<CreditContract> findById(String id) {
        return creditRepository.findById(id);
    }

    public List<CreditContract> findByCustomer(Customer customer) {
        return creditRepository.findByRentalRequestCustomer(customer);
    }

    public List<CreditContract> findByBank(Bank bank) {
        return creditRepository.findByGrantingBank(bank);
    }

    public List<CreditContract> findActiveCredits() {
        return creditRepository.findByStatus("ATIVO");
    }

    @Transactional
    public CreditContract grantCredit(String requestId, String bankId, double interestRate, int termInMonths) {
        RentalRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado"));

        Bank bank = bankRepository.findById(bankId)
                .orElseThrow(() -> new IllegalArgumentException("Banco não encontrado"));

        // Verificar se já existe contrato de crédito para este pedido
        if (creditRepository.existsByRentalRequest(request)) {
            throw new IllegalStateException("Já existe um contrato de crédito para este pedido");
        }

        // Validar capacidade do banco para conceder crédito
        CreditContract tempContract = new CreditContract();
        tempContract.setValue(request.getEstimatedValue());

        if (!bank.grantCredit(tempContract)) {
            throw new IllegalStateException("Banco não pode conceder este crédito");
        }

        CreditContract creditContract = new CreditContract();
        creditContract.setId(UUID.randomUUID().toString());
        creditContract.setRentalRequest(request);
        creditContract.setValue(request.getEstimatedValue());
        creditContract.setInterestRate(interestRate);
        creditContract.setTerm(termInMonths);
        creditContract.setGrantDate(LocalDate.now());
        creditContract.setStatus("ATIVO");
        creditContract.setGrantingBank(bank);

        return creditRepository.save(creditContract);
    }

    public double calculateMonthlyInstallment(String creditId) {
        CreditContract credit = creditRepository.findById(creditId)
                .orElseThrow(() -> new IllegalArgumentException("Contrato de crédito não encontrado"));

        return credit.calculateInstallment();
    }

    public double calculateTotalInterest(String creditId) {
        CreditContract credit = creditRepository.findById(creditId)
                .orElseThrow(() -> new IllegalArgumentException("Contrato de crédito não encontrado"));

        return credit.calculateTotalInterest();
    }

    @Transactional
    public CreditContract payInstallment(String creditId, double amount) {
        CreditContract credit = creditRepository.findById(creditId)
                .orElseThrow(() -> new IllegalArgumentException("Contrato de crédito não encontrado"));

        if (!"ATIVO".equals(credit.getStatus())) {
            throw new IllegalStateException("Contrato de crédito não está ativo");
        }

        double expectedInstallment = credit.calculateInstallment();
        if (Math.abs(amount - expectedInstallment) > 0.01) {
            throw new IllegalArgumentException("Valor da parcela incorreto. Esperado: " +
                    String.format("%.2f", expectedInstallment));
        }

        // Atualizar informações de pagamento
        credit.setLastPaymentDate(LocalDate.now());

        return creditRepository.save(credit);
    }

    @Transactional
    public void liquidateCredit(String creditId) {
        CreditContract credit = creditRepository.findById(creditId)
                .orElseThrow(() -> new IllegalArgumentException("Contrato de crédito não encontrado"));

        credit.setStatus("LIQUIDADO");
        credit.setLiquidationDate(LocalDate.now());
        creditRepository.save(credit);
    }

    public boolean evaluateCreditworthiness(Customer customer, double requestedAmount) {
        // Lógica simplificada de análise de crédito
        List<CreditContract> existingCredits = findByCustomer(customer);

        // Verificar se cliente tem créditos ativos
        long activeCredits = existingCredits.stream()
                .filter(credit -> "ATIVO".equals(credit.getStatus()))
                .count();

        if (activeCredits > 3) {
            return false; // Muitos créditos ativos
        }

        // Verificar valor total dos créditos ativos
        double totalActiveCredit = existingCredits.stream()
                .filter(credit -> "ATIVO".equals(credit.getStatus()))
                .mapToDouble(CreditContract::getValue)
                .sum();

        // Limite total de crédito por cliente
        double maxCreditLimit = 200000.0;

        return (totalActiveCredit + requestedAmount) <= maxCreditLimit;
    }

    public void delete(String id) {
        creditRepository.deleteById(id);
    }
}