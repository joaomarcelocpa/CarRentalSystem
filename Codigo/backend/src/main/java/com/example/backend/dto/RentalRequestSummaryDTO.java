package com.example.backend.dto;

import java.time.LocalDate;

public class RentalRequestSummaryDTO {
    private String id;
    private LocalDate desiredStartDate;  // Mantido para compatibilidade
    private LocalDate desiredEndDate;    // Mantido para compatibilidade
    private String status;
    private Double estimatedValue;       // Mantido para compatibilidade

    public RentalRequestSummaryDTO() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LocalDate getDesiredStartDate() {
        return desiredStartDate;
    }

    public void setDesiredStartDate(LocalDate desiredStartDate) {
        this.desiredStartDate = desiredStartDate;
    }

    public LocalDate getDesiredEndDate() {
        return desiredEndDate;
    }

    public void setDesiredEndDate(LocalDate desiredEndDate) {
        this.desiredEndDate = desiredEndDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Double getEstimatedValue() {
        return estimatedValue;
    }

    public void setEstimatedValue(Double estimatedValue) {
        this.estimatedValue = estimatedValue;
    }
}