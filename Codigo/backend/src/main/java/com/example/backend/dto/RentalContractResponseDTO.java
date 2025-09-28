package com.example.backend.dto;

import java.time.LocalDate;

public class RentalContractResponseDTO {
    
    private String id;
    private LocalDate startDate;
    private LocalDate endDate;
    private Double value;
    private LocalDate signingDate;
    private String status;
    private Integer renewalCount;
    private Long durationInDays;
    private Long daysRemaining;
    private boolean currentlyActive;
    
    // Informações do cliente
    private CustomerSummaryDTO customer;
    
    // Informações do automóvel
    private AutomobileSummaryDTO automobile;
    
    // Informações do crédito associado (se houver)
    private CreditContractSummaryDTO associatedCredit;
    
    public RentalContractResponseDTO() {}
    
    // Getters e Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    
    public Double getValue() { return value; }
    public void setValue(Double value) { this.value = value; }
    
    public LocalDate getSigningDate() { return signingDate; }
    public void setSigningDate(LocalDate signingDate) { this.signingDate = signingDate; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public Integer getRenewalCount() { return renewalCount; }
    public void setRenewalCount(Integer renewalCount) { this.renewalCount = renewalCount; }
    
    public Long getDurationInDays() { return durationInDays; }
    public void setDurationInDays(Long durationInDays) { this.durationInDays = durationInDays; }
    
    public Long getDaysRemaining() { return daysRemaining; }
    public void setDaysRemaining(Long daysRemaining) { this.daysRemaining = daysRemaining; }
    
    public boolean isCurrentlyActive() { return currentlyActive; }
    public void setCurrentlyActive(boolean currentlyActive) { this.currentlyActive = currentlyActive; }
    
    public CustomerSummaryDTO getCustomer() { return customer; }
    public void setCustomer(CustomerSummaryDTO customer) { this.customer = customer; }
    
    public AutomobileSummaryDTO getAutomobile() { return automobile; }
    public void setAutomobile(AutomobileSummaryDTO automobile) { this.automobile = automobile; }
    
    public CreditContractSummaryDTO getAssociatedCredit() { return associatedCredit; }
    public void setAssociatedCredit(CreditContractSummaryDTO associatedCredit) { this.associatedCredit = associatedCredit; }
}