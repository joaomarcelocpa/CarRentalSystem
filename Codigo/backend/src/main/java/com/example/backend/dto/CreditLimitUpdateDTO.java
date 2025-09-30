package com.example.backend.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class CreditLimitUpdateDTO {

    @NotNull(message = "Limite de crédito é obrigatório")
    @Min(value = 0, message = "Limite de crédito deve ser maior ou igual a zero")
    private Double credit_limit;

    public CreditLimitUpdateDTO() {}

    public CreditLimitUpdateDTO(Double credit_limit) {
        this.credit_limit = credit_limit;
    }

    public Double getCreditLimit() {
        return credit_limit;
    }

    public void setCreditLimit(Double credit_limit) {
        this.credit_limit = credit_limit;
    }
}