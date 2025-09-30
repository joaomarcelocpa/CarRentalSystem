package com.example.backend.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class CreditLimitUpdateDTO {

    @NotNull(message = "Limite de crédito é obrigatório")
    @Min(value = 0, message = "Limite de crédito deve ser maior ou igual a zero")
    private Double creditLimit;

    public CreditLimitUpdateDTO() {}

    public CreditLimitUpdateDTO(Double creditLimit) {
        this.creditLimit = creditLimit;
    }

    public Double getCreditLimit() {
        return creditLimit;
    }

    public void setCreditLimit(Double creditLimit) {
        this.creditLimit = creditLimit;
    }
}