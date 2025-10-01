package com.example.backend.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "credit_contract")
public class CreditContract {
    @Id
    private String id;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "bank_agent_id", nullable = false)
    private BankAgent bankAgent;

    @Column(name = "credit_limit", nullable = false)
    private Double creditLimit;

    @Column(name = "available_limit", nullable = false)
    private Double availableLimit;

    @Column(name = "created_at")
    private LocalDate createdAt;

    @Column(name = "updated_at")
    private LocalDate updatedAt;

    @Column(name = "status")
    private String status; // ACTIVE, INACTIVE, SUSPENDED

    public CreditContract() {
        this.status = "ACTIVE";
        this.createdAt = LocalDate.now();
        this.updatedAt = LocalDate.now();
    }

    // Verifica se há limite disponível suficiente
    public boolean hasAvailableLimit(Double amount) {
        if (amount == null || availableLimit == null) return false;
        return availableLimit >= amount;
    }

    // Reduz o limite disponível quando um pedido é aprovado
    public void reduceAvailableLimit(Double amount) {
        if (amount != null && availableLimit != null) {
            this.availableLimit = Math.max(0, this.availableLimit - amount);
            this.updatedAt = LocalDate.now();
        }
    }

    // Restaura o limite disponível quando um pedido é cancelado/rejeitado
    public void restoreAvailableLimit(Double amount) {
        if (amount != null && availableLimit != null && creditLimit != null) {
            this.availableLimit = Math.min(creditLimit, this.availableLimit + amount);
            this.updatedAt = LocalDate.now();
        }
    }

    // Getters e Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public BankAgent getBankAgent() {
        return bankAgent;
    }

    public void setBankAgent(BankAgent bankAgent) {
        this.bankAgent = bankAgent;
    }

    public Double getCreditLimit() {
        return creditLimit;
    }

    public void setCreditLimit(Double creditLimit) {
        this.creditLimit = creditLimit;
        // Quando o limite é atualizado, ajusta o limite disponível proporcionalmente
        if (this.availableLimit != null && creditLimit != null) {
            this.availableLimit = Math.min(this.availableLimit, creditLimit);
        } else {
            this.availableLimit = creditLimit;
        }
        this.updatedAt = LocalDate.now();
    }

    public Double getAvailableLimit() {
        return availableLimit;
    }

    public void setAvailableLimit(Double availableLimit) {
        this.availableLimit = availableLimit;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDate getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDate updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
        this.updatedAt = LocalDate.now();
    }
}