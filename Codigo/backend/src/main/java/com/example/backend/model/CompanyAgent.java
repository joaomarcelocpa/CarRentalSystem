package com.example.backend.model;

import com.example.backend.model.enums.UserRole;
import jakarta.persistence.Entity;

@Entity
public class CompanyAgent extends Agent {

    public CompanyAgent() {
        super();
        this.role = UserRole.AGENT_COMPANY;
    }
}
