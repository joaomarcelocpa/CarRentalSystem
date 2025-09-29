package com.example.backend.dto;

import com.example.backend.model.enums.RequestStatus;
import jakarta.validation.constraints.NotNull;

public class RentalRequestStatusUpdateDTO {

    @NotNull(message = "Status é obrigatório")
    private RequestStatus status;

    private String rejectionReason;

    public RentalRequestStatusUpdateDTO() {}

    public RequestStatus getStatus() {
        return status;
    }

    public void setStatus(RequestStatus status) {
        this.status = status;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }
}