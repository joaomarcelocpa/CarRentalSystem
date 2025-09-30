package com.example.backend.dto;

import jakarta.validation.constraints.NotBlank;

public class CustomerCreateDTO {
    @NotBlank(message = "Nome é obrigatório")
    private String name;

    public CustomerCreateDTO() {}

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}