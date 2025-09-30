package com.example.backend.model;

import com.example.backend.model.enums.UserRole;
import jakarta.persistence.*;
import java.util.List;

@Entity
public class Customer extends User {

    // Removidos: rg, cpf, address, profession, emailContact
    // O email já existe na classe User

    @Column(name = "credit_limit")
    private Double credit_limit;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
    private List<RentalRequest> rentalRequests;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
    private List<Income> incomes;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EmployerEntity> employers;

    public Customer() {
        super();
        this.role = UserRole.CUSTOMER;
        this.credit_limit = 0.0;
    }

    // Getters e Setters
    public Double getCreditLimit() { return credit_limit; }
    public void setCreditLimit(Double credit_limit) { this.credit_limit = credit_limit; }

    public List<RentalRequest> getRentalRequests() { return rentalRequests; }
    public void setRentalRequests(List<RentalRequest> rentalRequests) { this.rentalRequests = rentalRequests; }

    public List<Income> getIncomes() { return incomes; }
    public void setIncomes(List<Income> incomes) { this.incomes = incomes; }

    public List<EmployerEntity> getEmployers() { return employers; }
    public void setEmployers(List<EmployerEntity> employers) { this.employers = employers; }
}
