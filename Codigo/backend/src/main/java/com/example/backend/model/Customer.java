package com.example.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.util.List;


@Entity
public class Customer extends User {

    @NotBlank
    private String name;

    @Email
    private String emailContact;

    private String rg;
    private String cpf;
    private String address;
    private String profession;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
    private List<RentalRequest> rentalRequests;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
    private List<Income> incomes;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EmployerEntity> employers;

    public Customer() {}


    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmailContact() { return emailContact; }
    public void setEmailContact(String emailContact) { this.emailContact = emailContact; }

    public String getRg() { return rg; }
    public void setRg(String rg) { this.rg = rg; }

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getProfession() { return profession; }
    public void setProfession(String profession) { this.profession = profession; }

    public List<RentalRequest> getRentalRequests() { return rentalRequests; }
    public void setRentalRequests(List<RentalRequest> rentalRequests) { this.rentalRequests = rentalRequests; }

    public List<Income> getIncomes() { return incomes; }
    public void setIncomes(List<Income> incomes) { this.incomes = incomes; }

    public List<EmployerEntity> getEmployers() { return employers; }
    public void setEmployers(List<EmployerEntity> employers) { this.employers = employers; }
}
