package com.example.backend.dto;

import java.time.LocalDate;

public class CreditContractResponseDTO {
    
    private String id;
    private Double value;
    private Double interestRate;
    private Integer term;
    private LocalDate grantDate;
    private String status;
    private LocalDate lastPaymentDate;
    private LocalDate liquidationDate;
    private Double monthlyInstallment;
    private Double totalPayable;
    private Double totalInterest;
    private Integer remainingMonths;
    private boolean overdue;
    
    // Informações do cliente
    private CustomerSummaryDTO customer;
    
    // Informações do banco
    private BankSummaryDTO grantingBank;
    
    public CreditContractResponseDTO() {}
    
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
    
    public Double getMonthlyInstallment() { return monthlyInstallment; }
    public void setMonthlyInstallment(Double monthlyInstallment) { this.monthlyInstallment = monthlyInstallment; }
    
    public Double getTotalPayable() { return totalPayable; }
    public void setTotalPayable(Double totalPayable) { this.totalPayable = totalPayable; }
    
    public Double getTotalInterest() { return totalInterest; }
    public void setTotalInterest(Double totalInterest) { this.totalInterest = totalInterest; }
    
    public Integer getRemainingMonths() { return remainingMonths; }
    public void setRemainingMonths(Integer remainingMonths) { this.remainingMonths = remainingMonths; }
    
    public boolean isOverdue() { return overdue; }
    public void setOverdue(boolean overdue) { this.overdue = overdue; }
    
    public CustomerSummaryDTO getCustomer() { return customer; }
    public void setCustomer(CustomerSummaryDTO customer) { this.customer = customer; }
    
    public BankSummaryDTO getGrantingBank() { return grantingBank; }
    public void setGrantingBank(BankSummaryDTO grantingBank) { this.grantingBank = grantingBank; }
}