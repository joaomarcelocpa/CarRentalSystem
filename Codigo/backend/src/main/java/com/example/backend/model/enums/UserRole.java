package com.example.backend.model.enums;

public enum UserRole {
    CUSTOMER("cliente", "Cliente"),
    AGENT_COMPANY("agente-empresa", "Agente Empresa"),
    AGENT_BANK("agente-banco", "Agente Banco");

    private final String code;
    private final String description;

    UserRole(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static UserRole fromCode(String code) {
        for (UserRole role : values()) {
            if (role.code.equals(code)) {
                return role;
            }
        }
        throw new IllegalArgumentException("Invalid user role code: " + code);
    }

    public boolean isCustomer() {
        return this == CUSTOMER;
    }

    public boolean isAgent() {
        return this == AGENT_COMPANY || this == AGENT_BANK;
    }

    public boolean isAgentCompany() {
        return this == AGENT_COMPANY;
    }

    public boolean isAgentBank() {
        return this == AGENT_BANK;
    }
}
