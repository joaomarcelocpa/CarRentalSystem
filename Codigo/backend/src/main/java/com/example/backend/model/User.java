package com.example.backend.model;

import com.example.backend.model.enums.UserRole;
import jakarta.persistence.*;
import java.time.LocalDate;

@MappedSuperclass
public abstract class User {
    @Id
    private String id;
    private String username;
    private String password;
    private String email;
    private LocalDate createdAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_role", nullable = false)
    protected UserRole role;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public LocalDate getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDate createdAt) { this.createdAt = createdAt; }

    public UserRole getRole() { return role; }
    public void setRole(UserRole role) { this.role = role; }

    // Métodos de conveniência para verificação de roles
    public boolean isCustomer() {
        return role != null && role.isCustomer();
    }

    public boolean isAgent() {
        return role != null && role.isAgent();
    }

    public boolean isAgentCompany() {
        return role != null && role.isAgentCompany();
    }

    public boolean isAgentBank() {
        return role != null && role.isAgentBank();
    }
}
