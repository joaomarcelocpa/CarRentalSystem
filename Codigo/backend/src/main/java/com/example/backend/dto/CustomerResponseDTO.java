package com.example.backend.dto;

import java.time.LocalDate;
import java.util.List;

public class CustomerResponseDTO {
    private String id;
    private String username;
    private String email;
    private LocalDate createdAt;

    private Double creditLimit;
    private List<RentalRequestSummaryDTO> rentalRequests;

    // Campos removidos: rg, cpf, address, profession, emailContact
    // Agora usa os campos herdados de User (username, email)

    public CustomerResponseDTO() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public LocalDate getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDate createdAt) { this.createdAt = createdAt; }

    public Double getCreditLimit() { return creditLimit; }
    public void setCreditLimit(Double creditLimit) { this.creditLimit = creditLimit; }

    public List<RentalRequestSummaryDTO> getRentalRequests() { return rentalRequests; }
    public void setRentalRequests(List<RentalRequestSummaryDTO> rentalRequests) {
        this.rentalRequests = rentalRequests;
    }
}