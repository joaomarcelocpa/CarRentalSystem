package com.example.backend.model.enums;

public enum RequestStatus {
    PENDING("Pendente"),
    UNDER_ANALYSIS("Em Análise"),
    APPROVED("Aprovado"),
    REJECTED("Rejeitado"),
    CANCELLED("Cancelado"),
    ACTIVE("Ativo"),
    COMPLETED("Concluído");

    private final String description;

    RequestStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public boolean isPending() {
        return this == PENDING;
    }

    public boolean canBeModified() {
        return this == PENDING || this == UNDER_ANALYSIS;
    }

    public boolean canBeCancelled() {
        return this == PENDING || this == UNDER_ANALYSIS || this == APPROVED;
    }
}