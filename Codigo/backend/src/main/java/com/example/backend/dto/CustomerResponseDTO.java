package com.example.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.util.List;

public class CustomerResponseDTO {
    private String id;
    private String name;
    private String emailContact;
    private String rg;
    private String cpf;
    private String address;
    private String profession;
    private LocalDate createdAt;
    private Double creditLimit;

    private List<RentalRequestSummaryDTO> rentalRequests;

    public CustomerResponseDTO() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmailContact() { return emailContact; }
    public void setEmailContact(String emailContact) { this.emailContact = emailContact; }
    public String getRg() { return rg; }
    public void setRg(String rg) { this.rg = rg; }
    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getProfession() { return profession; }
    public void setProfession(String profession) { this.profession = profession; }
    public LocalDate getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDate createdAt) { this.createdAt = createdAt; }
    public Double getCreditLimit() { return creditLimit; }
    public void setCreditLimit(Double creditLimit) { this.creditLimit = creditLimit; }
    public List<RentalRequestSummaryDTO> getRentalRequests() { return rentalRequests; }
    public void setRentalRequests(List<RentalRequestSummaryDTO> rentalRequests) { this.rentalRequests = rentalRequests; }
}