package com.example.backend.dto;

import com.example.backend.model.enums.UserRole;
import java.time.LocalDate;

public class UserResponseDTO {
    private String id;
    private String username;
    private String email;
    private UserRole role;
    private LocalDate createdAt;

    public UserResponseDTO() {}

    public UserResponseDTO(String id, String username, String email, UserRole role, LocalDate createdAt) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.role = role;
        this.createdAt = createdAt;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public UserRole getRole() { return role; }
    public void setRole(UserRole role) { this.role = role; }

    public LocalDate getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDate createdAt) { this.createdAt = createdAt; }
}
