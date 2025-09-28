package com.example.backend.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CreditGrantDTO {
    
    @NotBlank(message = "ID do pedido é obrigatório")
    private String requestId;
    
    @NotBlank(message = "ID do banco é obrigatório")
    private String bankId;
    
    @NotNull(message = "Taxa de juros é obrigatória")
    @Min(value = 0, message = "Taxa de juros deve ser positiva")
    @Max(value = 50, message = "Taxa de juros não pode exceder 50%")
    private Double interestRate;
    
    @NotNull(message = "Prazo em meses é obrigatório")
    @Min(value = 1, message = "Prazo deve ser de pelo menos 1 mês")
    @Max(value = 240, message = "Prazo não pode exceder 240 meses")
    private Integer termInMonths;
    
    private String observations;
    
    public CreditGrantDTO() {}
    
    public CreditGrantDTO(String requestId, String bankId, Double interestRate, 
                         Integer termInMonths, String observations) {
        this.requestId = requestId;
        this.bankId = bankId;
        this.interestRate = interestRate;
        this.termInMonths = termInMonths;
        this.observations = observations;
    }
    
    public String getRequestId() { return requestId; }
    public void setRequestId(String requestId) { this.requestId = requestId; }
    
    public String getBankId() { return bankId; }
    public void setBankId(String bankId) { this.bankId = bankId; }
    
    public Double getInterestRate() { return interestRate; }
    public void setInterestRate(Double interestRate) { this.interestRate = interestRate; }
    
    public Integer getTermInMonths() { return termInMonths; }
    public void setTermInMonths(Integer termInMonths) { this.termInMonths = termInMonths; }
    
    public String getObservations() { return observations; }
    public void setObservations(String observations) { this.observations = observations; }
}
