package com.example.backend.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class CreditContractUpdateDTO {

    @NotNull(message = "Limite de crédito é obrigatório")
    @Min(value = 0, message = "Limite de crédito deve ser maior ou igual a 0")
    private Double creditLimit;

    private String status; // ACTIVE, INACTIVE, SUSPENDED

    public CreditContractUpdateDTO() {}

    public CreditContractUpdateDTO(Double creditLimit, String status) {
        this.creditLimit = creditLimit;
        this.status = status;
    }

    public Double getCreditLimit() {
        return creditLimit;
    }

    public void setCreditLimit(Double creditLimit) {
        this.creditLimit = creditLimit;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}