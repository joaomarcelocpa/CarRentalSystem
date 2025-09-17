package com.example.backend.model;

import com.example.backend.model.enums.RequestStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Entity
public class RentalRequest {
    @Id
    private String id;

    @NotNull
    private LocalDate desiredStartDate;

    @NotNull
    private LocalDate desiredEndDate;

    @Enumerated(EnumType.STRING)
    private RequestStatus status;

    private LocalDate creationDate;
    private Double estimatedValue;
    private String observations;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "automobile_id")
    private Automobile automobile;

    public RentalRequest() {}

    public Double calculateValue() {
        if (automobile == null || desiredStartDate == null || desiredEndDate == null) return 0.0;
        long days = ChronoUnit.DAYS.between(desiredStartDate, desiredEndDate);
        if (days < 0) days = 0;
        return automobile.getDailyRate() == null ? 0.0 : automobile.getDailyRate() * days;
    }

    public void changeStatus(RequestStatus newStatus) {
        this.status = newStatus;
    }

    public boolean validateDates() {
        return desiredStartDate != null && desiredEndDate != null && !desiredEndDate.isBefore(desiredStartDate);
    }

    // getters/setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public LocalDate getDesiredStartDate() { return desiredStartDate; }
    public void setDesiredStartDate(LocalDate desiredStartDate) { this.desiredStartDate = desiredStartDate; }
    public LocalDate getDesiredEndDate() { return desiredEndDate; }
    public void setDesiredEndDate(LocalDate desiredEndDate) { this.desiredEndDate = desiredEndDate; }
    public RequestStatus getStatus() { return status; }
    public void setStatus(RequestStatus status) { this.status = status; }
    public LocalDate getCreationDate() { return creationDate; }
    public void setCreationDate(LocalDate creationDate) { this.creationDate = creationDate; }
    public Double getEstimatedValue() { return estimatedValue; }
    public void setEstimatedValue(Double estimatedValue) { this.estimatedValue = estimatedValue; }
    public String getObservations() { return observations; }
    public void setObservations(String observations) { this.observations = observations; }
    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }
    public Automobile getAutomobile() { return automobile; }
    public void setAutomobile(Automobile automobile) { this.automobile = automobile; }
}
