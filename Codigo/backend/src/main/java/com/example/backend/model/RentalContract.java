package com.example.backend.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class RentalContract {
    @Id
    private String id;

    private LocalDate startDate;
    private LocalDate endDate;
    private Double value;
    private LocalDate signingDate;
    @Lob
    private String terms;

    @OneToOne
    @JoinColumn(name = "rental_request_id")
    private RentalRequest rentalRequest;

    public RentalContract() {}

    public Double calculateTotalValue() {
        return value;
    }

    public void renew(LocalDate newEndDate) {
        this.endDate = newEndDate;
    }

    public void finalizeContract() {

    }

    // getters/setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public Double getValue() { return value; }
    public void setValue(Double value) { this.value = value; }
    public LocalDate getSigningDate() { return signingDate; }
    public void setSigningDate(LocalDate signingDate) { this.signingDate = signingDate; }
    public String getTerms() { return terms; }
    public void setTerms(String terms) { this.terms = terms; }
    public RentalRequest getRentalRequest() { return rentalRequest; }
    public void setRentalRequest(RentalRequest rentalRequest) { this.rentalRequest = rentalRequest; }
}
