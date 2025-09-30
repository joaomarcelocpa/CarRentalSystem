package com.example.backend.dto;

public class CustomerSummaryDTO {
    private String id;
    private String username;
    private String email;

    // Campo emailContact removido, usando email do User

    public CustomerSummaryDTO() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}