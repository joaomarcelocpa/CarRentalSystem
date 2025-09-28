package com.example.backend.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class CreditPaymentDTO {
    
    @NotNull(message = "Valor do pagamento é obrigatório")
    @Min(value = 0, message = "Valor do pagamento deve ser positivo")
    private Double amount;
    
    private LocalDate paymentDate = LocalDate.now();
    
    private String paymentMethod;
    
    private String observations;
    
    public CreditPaymentDTO() {}
    
    public CreditPaymentDTO(Double amount, LocalDate paymentDate, 
                           String paymentMethod, String observations) {
        this.amount = amount;
        this.paymentDate = paymentDate;
        this.paymentMethod = paymentMethod;
        this.observations = observations;
    }
    
    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }
    
    public LocalDate getPaymentDate() { return paymentDate; }
    public void setPaymentDate(LocalDate paymentDate) { this.paymentDate = paymentDate; }
    
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    
    public String getObservations() { return observations; }
    public void setObservations(String observations) { this.observations = observations; }
}
