package com.example.backend.dto;

import com.example.backend.model.enums.UserRole;

public class LoginResponseDTO {
    private String token;
    private String type = "Bearer";
    private String username;
    private String email;
    private UserRole role;
    private Long expiresIn;

    public LoginResponseDTO() {}

    public LoginResponseDTO(String token, String username, String email, UserRole role, Long expiresIn) {
        this.token = token;
        this.username = username;
        this.email = email;
        this.role = role;
        this.expiresIn = expiresIn;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public UserRole getRole() { return role; }
    public void setRole(UserRole role) { this.role = role; }

    public Long getExpiresIn() { return expiresIn; }
    public void setExpiresIn(Long expiresIn) { this.expiresIn = expiresIn; }
}
