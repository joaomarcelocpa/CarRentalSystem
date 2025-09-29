package com.example.backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Company {
    @Id
    private String id;
    private String sector;

    public Company() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSector() {
        return sector;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }

    public boolean evaluateRentalRequest(RentalRequest request) {
        // CORRIGIDO: usar getTotalValue() ao invés de getEstimatedValue()
        return request != null && request.getTotalValue() != null && request.getTotalValue() < 10000;
    }
}