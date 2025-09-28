package com.example.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

@Entity
@Table(name = "credit_contracts")
public class CreditContract {

    @Id
    private String id;

    @NotNull
    @Min(0)
    @Column(name = "contract_value", nullable = false)
    private Double value;

    @NotNull
    @Min(0)
    @Column(name = "interest_rate", nullable = false)
    private Double interestRate;

    @NotNull
    @Min(1)
    @Column(name = "term_months", nullable = false)
    private Integer term;

    @NotNull
    @Column(name = "grant_date", nullable = false)
    private LocalDate grantDate;

    @Column(name = "status", nullable = false)
    private String status = "ATIVO";

    @Column(name = "last_payment_date")
    private LocalDate lastPaymentDate;

    @Column(name = "liquidation_date")
    private LocalDate liquidationDate;

    @Column(name = "observations", columnDefinition = "TEXT")
    private String observations;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rental_request_id", unique = true)
    private RentalRequest rentalRequest;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "granting_bank_id")
    private Bank grantingBank;

    public CreditContract() {}

    public CreditContract(Double value, Double interestRate, Integer term,
                          RentalRequest rentalRequest, Bank grantingBank) {
        this.value = value;
        this.interestRate = interestRate;
        this.term = term;
        this.rentalRequest = rentalRequest;
        this.grantingBank = grantingBank;
        this.grantDate = LocalDate.now();
    }

    /**
     * Calcula o valor da parcela mensal usando a fórmula de juros compostos
     * Conforme método do diagrama: calculateInstallment(): Double
     */
    public Double calculateInstallment() {
        if (interestRate == null || term == null || term == 0 || value == null) {
            return 0.0;
        }

        double monthlyRate = interestRate / 100.0;
        if (monthlyRate == 0) {
            return value / term;
        }

        return (value * monthlyRate * Math.pow(1 + monthlyRate, term)) /
                (Math.pow(1 + monthlyRate, term) - 1);
    }

    /**
     * Verifica o status do contrato
     * Conforme método do diagrama: checkStatus(): String
     */
    public String checkStatus() {
        if (liquidationDate != null) {
            return "LIQUIDADO";
        }

        if (grantDate != null && term != null) {
            LocalDate expectedEndDate = grantDate.plusMonths(term);
            if (LocalDate.now().isAfter(expectedEndDate) && "ATIVO".equals(status)) {
                return "EM_ATRASO";
            }
        }

        return status;
    }

    /**
     * Calcula o valor total a ser pago (principal + juros)
     */
    public Double calculateTotalPayable() {
        return calculateInstallment() * term;
    }

    /**
     * Calcula o total de juros a serem pagos
     */
    public Double calculateTotalInterest() {
        return calculateTotalPayable() - value;
    }

    /**
     * Calcula quantos meses restam para liquidação
     */
    public Integer getRemainingMonths() {
        if (grantDate == null || term == null) return null;

        LocalDate endDate = grantDate.plusMonths(term);
        LocalDate now = LocalDate.now();

        if (now.isAfter(endDate)) return 0;

        return (int) java.time.temporal.ChronoUnit.MONTHS.between(now, endDate);
    }

    /**
     * Verifica se o contrato está em atraso
     */
    public boolean isOverdue() {
        if (grantDate == null || term == null || !"ATIVO".equals(status)) {
            return false;
        }

        LocalDate expectedEndDate = grantDate.plusMonths(term);
        return LocalDate.now().isAfter(expectedEndDate);
    }

    /**
     * Calcula a taxa efetiva anual
     */
    public Double calculateAnnualEffectiveRate() {
        if (interestRate == null) return 0.0;
        double monthlyRate = interestRate / 100.0;
        return (Math.pow(1 + monthlyRate, 12) - 1) * 100;
    }

    // Getters e Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public Double getValue() { return value; }
    public void setValue(Double value) { this.value = value; }

    public Double getInterestRate() { return interestRate; }
    public void setInterestRate(Double interestRate) { this.interestRate = interestRate; }

    public Integer getTerm() { return term; }
    public void setTerm(Integer term) { this.term = term; }

    public LocalDate getGrantDate() { return grantDate; }
    public void setGrantDate(LocalDate grantDate) { this.grantDate = grantDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDate getLastPaymentDate() { return lastPaymentDate; }
    public void setLastPaymentDate(LocalDate lastPaymentDate) { this.lastPaymentDate = lastPaymentDate; }

    public LocalDate getLiquidationDate() { return liquidationDate; }
    public void setLiquidationDate(LocalDate liquidationDate) { this.liquidationDate = liquidationDate; }

    public String getObservations() { return observations; }
    public void setObservations(String observations) { this.observations = observations; }

    public RentalRequest getRentalRequest() { return rentalRequest; }
    public void setRentalRequest(RentalRequest rentalRequest) { this.rentalRequest = rentalRequest; }

    public Bank getGrantingBank() { return grantingBank; }
    public void setGrantingBank(Bank grantingBank) { this.grantingBank = grantingBank; }
}