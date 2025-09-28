package com.example.backend.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class ContractRenewalDTO {
    
    @NotNull(message = "Nova data de término é obrigatória")
    @Future(message = "Nova data de término deve ser futura")
    private LocalDate newEndDate;
    
    private String renewalReason;
    
    public ContractRenewalDTO() {}
    
    public ContractRenewalDTO(LocalDate newEndDate, String renewalReason) {
        this.newEndDate = newEndDate;
        this.renewalReason = renewalReason;
    }
    
    public LocalDate getNewEndDate() { return newEndDate; }
    public void setNewEndDate(LocalDate newEndDate) { this.newEndDate = newEndDate; }
    
    public String getRenewalReason() { return renewalReason; }
    public void setRenewalReason(String renewalReason) { this.renewalReason = renewalReason; }
}