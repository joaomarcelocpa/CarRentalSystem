package com.example.backend.model;

import jakarta.persistence.Entity;

@Entity
public class Agent extends User {

    private String corporateReason;
    private String cnpj;

    public Agent() {
        super();
        // Role será definido pelas subclasses
    }

    public String getCorporateReason() { return corporateReason; }
    public void setCorporateReason(String corporateReason) { this.corporateReason = corporateReason; }

    public String getCnpj() { return cnpj; }
    public void setCnpj(String cnpj) { this.cnpj = cnpj; }
}
