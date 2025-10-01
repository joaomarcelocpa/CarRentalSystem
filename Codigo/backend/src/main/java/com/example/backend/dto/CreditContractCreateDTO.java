package com.example.backend.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CreditContractCreateDTO {

    @NotBlank(message = "ID do cliente é obrigatório")
    private String customerId;

    @NotNull(message = "Limite de crédito é obrigatório")
    @Min(value = 0, message = "Limite de crédito deve ser maior ou igual a 0")
    private Double creditLimit;

    public CreditContractCreateDTO() {}

    public CreditContractCreateDTO(String customerId, Double creditLimit) {
        this.customerId = customerId;
        this.creditLimit = creditLimit;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public Double getCreditLimit() {
        return creditLimit;
    }

    public void setCreditLimit(Double creditLimit) {
        this.creditLimit = creditLimit;
    }
}