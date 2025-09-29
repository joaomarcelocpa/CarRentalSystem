package com.example.backend.model;

import com.example.backend.model.enums.RequestStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Entity
@Table(name = "rental_request")
public class RentalRequest {

    @Id
    private String id;

    @NotNull
    @Column(name = "pickup_date", nullable = false)
    private LocalDate pickupDate;

    @NotNull
    @Column(name = "return_date", nullable = false)
    private LocalDate returnDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RequestStatus status;

    @Column(name = "created_at")
    private LocalDate createdAt;

    @Column(name = "total_value")
    private Double totalValue;

    @Column(name = "rental_days")
    private Integer rentalDays;

    @Column(length = 1000)
    private String observations;

    @Column(name = "processed_by_agent_id")
    private String processedByAgentId;

    @Column(name = "processed_by_agent_username")
    private String processedByAgentUsername;

    @Column(name = "processed_at")
    private LocalDate processedAt;

    @Column(name = "rejection_reason", length = 500)
    private String rejectionReason;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "automobile_id", nullable = false)
    private Automobile automobile;

    public RentalRequest() {
        this.status = RequestStatus.PENDING;
        this.createdAt = LocalDate.now();
    }

    public void calculateTotalValue() {
        if (automobile == null || pickupDate == null || returnDate == null) {
            this.totalValue = 0.0;
            this.rentalDays = 0;
            return;
        }

        long days = ChronoUnit.DAYS.between(pickupDate, returnDate);
        if (days < 0) days = 0;

        this.rentalDays = (int) days;
        this.totalValue = automobile.getDailyRate() == null ? 0.0 : automobile.getDailyRate() * days;
    }

    public void changeStatus(RequestStatus newStatus, String agentId, String agentUsername) {
        this.status = newStatus;
        this.processedByAgentId = agentId;
        this.processedByAgentUsername = agentUsername;
        this.processedAt = LocalDate.now();
    }

    public boolean validateDates() {
        if (pickupDate == null || returnDate == null) return false;
        if (returnDate.isBefore(pickupDate)) return false;
        if (pickupDate.isBefore(LocalDate.now())) return false;
        return true;
    }

    public boolean canBeModified() {
        return status != null && status.canBeModified();
    }

    public boolean canBeCancelled() {
        return status != null && status.canBeCancelled();
    }

    // GETTERS E SETTERS
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public LocalDate getPickupDate() { return pickupDate; }
    public void setPickupDate(LocalDate pickupDate) {
        this.pickupDate = pickupDate;
        calculateTotalValue();
    }

    public LocalDate getReturnDate() { return returnDate; }
    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
        calculateTotalValue();
    }

    public RequestStatus getStatus() { return status; }
    public void setStatus(RequestStatus status) { this.status = status; }

    public LocalDate getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDate createdAt) { this.createdAt = createdAt; }

    public Double getTotalValue() { return totalValue; }
    public void setTotalValue(Double totalValue) { this.totalValue = totalValue; }

    public Integer getRentalDays() { return rentalDays; }
    public void setRentalDays(Integer rentalDays) { this.rentalDays = rentalDays; }

    public String getObservations() { return observations; }
    public void setObservations(String observations) { this.observations = observations; }

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

    public String getRejectionReason() { return rejectionReason; }
    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }

    public Automobile getAutomobile() { return automobile; }
    public void setAutomobile(Automobile automobile) {
        this.automobile = automobile;
        calculateTotalValue();
    }
}