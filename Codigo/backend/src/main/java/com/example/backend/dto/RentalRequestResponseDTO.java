package com.example.backend.dto;

import com.example.backend.model.enums.RequestStatus;
import java.time.LocalDate;

public class RentalRequestResponseDTO {

    private String id;
    private LocalDate pickupDate;
    private LocalDate returnDate;
    private RequestStatus status;
    private String statusDescription;
    private LocalDate createdAt;
    private Double totalValue;
    private Integer rentalDays;
    private String observations;
    private CustomerSummaryDTO customer;
    private AutomobileSummaryDTO automobile;
    private String processedByAgentId;
    private String processedByAgentUsername;
    private LocalDate processedAt;

    public RentalRequestResponseDTO() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public LocalDate getPickupDate() { return pickupDate; }
    public void setPickupDate(LocalDate pickupDate) { this.pickupDate = pickupDate; }

    public LocalDate getReturnDate() { return returnDate; }
    public void setReturnDate(LocalDate returnDate) { this.returnDate = returnDate; }

    public RequestStatus getStatus() { return status; }
    public void setStatus(RequestStatus status) { this.status = status; }

    public String getStatusDescription() { return statusDescription; }
    public void setStatusDescription(String statusDescription) {
        this.statusDescription = statusDescription;
    }

    public LocalDate getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDate createdAt) { this.createdAt = createdAt; }

    public Double getTotalValue() { return totalValue; }
    public void setTotalValue(Double totalValue) { this.totalValue = totalValue; }

    public Integer getRentalDays() { return rentalDays; }
    public void setRentalDays(Integer rentalDays) { this.rentalDays = rentalDays; }

    public String getObservations() { return observations; }
    public void setObservations(String observations) { this.observations = observations; }

    public CustomerSummaryDTO getCustomer() { return customer; }
    public void setCustomer(CustomerSummaryDTO customer) { this.customer = customer; }

    public AutomobileSummaryDTO getAutomobile() { return automobile; }
    public void setAutomobile(AutomobileSummaryDTO automobile) {
        this.automobile = automobile;
    }

    public String getProcessedByAgentId() { return processedByAgentId; }
    public void setProcessedByAgentId(String processedByAgentId) {
        this.processedByAgentId = processedByAgentId;
    }

    public String getProcessedByAgentUsername() { return processedByAgentUsername; }
    public void setProcessedByAgentUsername(String processedByAgentUsername) {
        this.processedByAgentUsername = processedByAgentUsername;
    }

    public LocalDate getProcessedAt() { return processedAt; }
    public void setProcessedAt(LocalDate processedAt) { this.processedAt = processedAt; }
}