package com.example.backend.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class CreditContract {
    @Id
    private String id;

    @Column(name = "contract_value")
    private Double value;

    private Double interestRate;

    private Integer term;

    private LocalDate grantDate;

    private String status;

    @OneToOne
    @JoinColumn(name = "rental_request_id")
    private RentalRequest rentalRequest;

    public CreditContract() {}

    public Double calculateInstallment() {
        if (interestRate == null || term == null || term == 0 || value == null) return 0.0;
        double monthlyRate = interestRate / 100.0;
        return (value * monthlyRate) / (1 - Math.pow(1 + monthlyRate, -term));
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public Double getValue() { return value; }
    public void setValue(Double value) { this.value = value; }
    public Double getInterestRate() { return interestRate; }
    public void setInterestRate(Double interestRate) { this.interestRate = interestRate; }
    public Integer getTerm() { return term; }
    public void setTerm(Integer term) { this.term = term; }
    public LocalDate getGrantDate() { return grantDate; }
    public void setGrantDate(LocalDate grantDate) { this.grantDate = grantDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public RentalRequest getRentalRequest() { return rentalRequest; }
    public void setRentalRequest(RentalRequest rentalRequest) { this.rentalRequest = rentalRequest; }
}
