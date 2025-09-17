package com.example.backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class EmployerEntity {
    @Id
    private String id;
    private String name;
    private String cnpj;
    private String address;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;  // <--- ESTE CAMPO É O QUE O mappedBy ESPERA

    public EmployerEntity() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCnpj() { return cnpj; }
    public void setCnpj(String cnpj) { this.cnpj = cnpj; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
}
