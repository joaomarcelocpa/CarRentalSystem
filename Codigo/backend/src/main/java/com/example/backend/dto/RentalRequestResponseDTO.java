package com.example.backend.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class RentalRequestResponseDTO {
    private String id;
    private LocalDate desiredStartDate;
    private LocalDate desiredEndDate;
    private String status;
    private LocalDate creationDate;
    private Double estimatedValue;
    private String observations;

    private CustomerSummaryDTO customer;
    private AutomobileSummaryDTO automobile;

    public RentalRequestResponseDTO() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public LocalDate getDesiredStartDate() { return desiredStartDate; }
    public void setDesiredStartDate(LocalDate desiredStartDate) { this.desiredStartDate = desiredStartDate; }
    public LocalDate getDesiredEndDate() { return desiredEndDate; }
    public void setDesiredEndDate(LocalDate desiredEndDate) { this.desiredEndDate = desiredEndDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDate getCreationDate() { return creationDate; }
    public void setCreationDate(LocalDate creationDate) { this.creationDate = creationDate; }
    public Double getEstimatedValue() { return estimatedValue; }
    public void setEstimatedValue(Double estimatedValue) { this.estimatedValue = estimatedValue; }
    public String getObservations() { return observations; }
    public void setObservations(String observations) { this.observations = observations; }
    public CustomerSummaryDTO getCustomer() { return customer; }
    public void setCustomer(CustomerSummaryDTO customer) { this.customer = customer; }
    public AutomobileSummaryDTO getAutomobile() { return automobile; }
    public void setAutomobile(AutomobileSummaryDTO automobile) { this.automobile = automobile; }
}

