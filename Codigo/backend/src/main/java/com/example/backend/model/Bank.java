package com.example.backend.model;

import com.example.backend.model.enums.UserRole;
import jakarta.persistence.Entity;

@Entity
public class Bank extends User {
    private String bankCode;

    public Bank() {
        super();
        this.role = UserRole.AGENT_BANK;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public boolean grantCredit(CreditContract contract) {
        if (contract == null || contract.getValue() == null) return false;
        return contract.getValue() < 100000;
    }

    public boolean evaluateRentalRequest(RentalRequest request) {
        return request != null && (request.getTotalValue() == null || request.getTotalValue() < 50000);
    }
}