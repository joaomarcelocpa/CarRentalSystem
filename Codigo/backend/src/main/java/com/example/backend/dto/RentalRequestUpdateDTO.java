package com.example.backend.dto;

import jakarta.validation.constraints.Future;
import java.time.LocalDate;

public class RentalRequestUpdateDTO {

    @Future(message = "Data de retirada deve ser no futuro")
    private LocalDate pickupDate;

    @Future(message = "Data de devolução deve ser no futuro")
    private LocalDate returnDate;

    private String observations;

    public RentalRequestUpdateDTO() {}

    public LocalDate getPickupDate() {
        return pickupDate;
    }

    public void setPickupDate(LocalDate pickupDate) {
        this.pickupDate = pickupDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    public String getObservations() {
        return observations;
    }

    public void setObservations(String observations) {
        this.observations = observations;
    }
}