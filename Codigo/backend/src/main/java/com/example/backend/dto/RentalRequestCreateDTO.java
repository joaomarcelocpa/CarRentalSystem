package com.example.backend.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class RentalRequestCreateDTO {
    @NotNull
    private LocalDate desiredStartDate;

    @NotNull
    private LocalDate desiredEndDate;

    private String observations;

    @NotNull
    private String customerId;

    @NotNull
    private String automobileId;

    public RentalRequestCreateDTO() {}

    public LocalDate getDesiredStartDate() { return desiredStartDate; }
    public void setDesiredStartDate(LocalDate desiredStartDate) { this.desiredStartDate = desiredStartDate; }
    public LocalDate getDesiredEndDate() { return desiredEndDate; }
    public void setDesiredEndDate(LocalDate desiredEndDate) { this.desiredEndDate = desiredEndDate; }
    public String getObservations() { return observations; }
    public void setObservations(String observations) { this.observations = observations; }
    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }
    public String getAutomobileId() { return automobileId; }
    public void setAutomobileId(String automobileId) { this.automobileId = automobileId; }
}