package com.example.backend.dto;

import jakarta.validation.constraints.NotBlank;

public class ContractFinalizationDTO {
    
    @NotBlank(message = "Motivo da finalização é obrigatório")
    private String finalizationReason;
    
    private String observations;
    
    public ContractFinalizationDTO() {}
    
    public ContractFinalizationDTO(String finalizationReason, String observations) {
        this.finalizationReason = finalizationReason;
        this.observations = observations;
    }
    
    public String getFinalizationReason() { return finalizationReason; }
    public void setFinalizationReason(String finalizationReason) { this.finalizationReason = finalizationReason; }
    
    public String getObservations() { return observations; }
    public void setObservations(String observations) { this.observations = observations; }
}