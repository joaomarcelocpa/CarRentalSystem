package com.example.backend.dto;

public class CreditContractSummaryDTO {
    
    private String id;
    private Double value;
    private Double interestRate;
    private Integer term;
    private String status;
    private Double monthlyInstallment;
    
    public CreditContractSummaryDTO() {}
    
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public Double getValue() { return value; }
    public void setValue(Double value) { this.value = value; }
    
    public Double getInterestRate() { return interestRate; }
    public void setInterestRate(Double interestRate) { this.interestRate = interestRate; }
    
    public Integer getTerm() { return term; }
    public void setTerm(Integer term) { this.term = term; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public Double getMonthlyInstallment() { return monthlyInstallment; }
    public void setMonthlyInstallment(Double monthlyInstallment) { this.monthlyInstallment = monthlyInstallment; }
}