package com.example.backend.model;

import com.example.backend.model.enums.UserRole;
import jakarta.persistence.Entity;

@Entity
public class BankAgent extends Agent {

    public BankAgent() {
        super();
        this.role = UserRole.AGENT_BANK;
    }
}
