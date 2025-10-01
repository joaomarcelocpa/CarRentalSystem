package com.example.backend.dto;

import java.time.LocalDate;

public class CreditContractResponseDTO {

    private String id;
    private CustomerSummaryDTO customer;
    private String bankAgentId;
    private String bankAgentUsername;
    private Double creditLimit;
    private Double availableLimit;
    private Double usedLimit;
    private Double usagePercentage;
    private LocalDate createdAt;
    private LocalDate updatedAt;
    private String status;

    public CreditContractResponseDTO() {}

    // Getters e Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public CustomerSummaryDTO getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerSummaryDTO customer) {
        this.customer = customer;
    }

    public String getBankAgentId() {
        return bankAgentId;
    }

    public void setBankAgentId(String bankAgentId) {
        this.bankAgentId = bankAgentId;
    }

    public String getBankAgentUsername() {
        return bankAgentUsername;
    }

    public void setBankAgentUsername(String bankAgentUsername) {
        this.bankAgentUsername = bankAgentUsername;
    }

    public Double getCreditLimit() {
        return creditLimit;
    }

    public void setCreditLimit(Double creditLimit) {
        this.creditLimit = creditLimit;
    }

    public Double getAvailableLimit() {
        return availableLimit;
    }

    public void setAvailableLimit(Double availableLimit) {
        this.availableLimit = availableLimit;
    }

    public Double getUsedLimit() {
        return usedLimit;
    }

    public void setUsedLimit(Double usedLimit) {
        this.usedLimit = usedLimit;
    }

    public Double getUsagePercentage() {
        return usagePercentage;
    }

    public void setUsagePercentage(Double usagePercentage) {
        this.usagePercentage = usagePercentage;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDate getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDate updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}