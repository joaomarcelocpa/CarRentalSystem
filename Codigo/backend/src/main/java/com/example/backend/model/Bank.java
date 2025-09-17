package com.example.backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Bank {
    @Id
    private String id;

    private String bankCode;

    public Bank() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getBankCode() { return bankCode; }
    public void setBankCode(String bankCode) { this.bankCode = bankCode; }

    public boolean grantCredit(CreditContract contract) {
        if (contract == null || contract.getValue() == null) return false;
        return contract.getValue() < 100000;
    }

    public boolean evaluateRentalRequest(RentalRequest request) {
        return request != null && (request.getEstimatedValue() == null || request.getEstimatedValue() < 50000);
    }
}
