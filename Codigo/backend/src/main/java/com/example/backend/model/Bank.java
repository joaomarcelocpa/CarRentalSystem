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
}