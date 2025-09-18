package com.example.backend.model;

import com.example.backend.model.enums.IncomeType;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class Income {
    @Id
    private String id;

    @Column(name = "income_value")
    private Double value;

    @Enumerated(EnumType.STRING)
    private IncomeType type;

    private LocalDate startDate;
    private LocalDate endDate;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    public Income() {}

    public Double calculateAnnualIncome() {
        return value == null ? 0.0 : value * 12;
    }

    // getters/setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public Double getValue() { return value; }
    public void setValue(Double value) { this.value = value; }
    public IncomeType getType() { return type; }
    public void setType(IncomeType type) { this.type = type; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }
}
