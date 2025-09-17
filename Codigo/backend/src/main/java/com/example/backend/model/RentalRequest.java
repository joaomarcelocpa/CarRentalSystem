package com.example.backend.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class RentalRequest {
    @Id
    private String id;

    private LocalDate startDate;
    private LocalDate endDate;
    private Double estimatedValue;

    @Enumerated(EnumType.STRING)
    private RequestStatus status;

    public RentalRequest() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public Double getEstimatedValue() { return estimatedValue; }
    public void setEstimatedValue(Double estimatedValue) { this.estimatedValue = estimatedValue; }

    public RequestStatus getStatus() { return status; }
    public void setStatus(RequestStatus status) { this.status = status; }
}
