package com.example.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.util.List;

@Entity
public class Automobile {
    @Id
    private String id;

    private String registration;
    private int year;
    @NotBlank
    private String brand;
    @NotBlank
    private String model;
    private String licensePlate;
    private boolean available;
    @Min(0)
    private Double dailyRate;

    @OneToMany(mappedBy = "automobile", cascade = CascadeType.ALL)
    private List<RentalRequest> rentalRequests;

    public Automobile() {}

    public boolean checkAvailability(LocalDate start, LocalDate end) {
        if (!available) return false;
        // For simplicity: available flag controls availability
        return true;
    }

    public double calculatePeriodValue(int days) {
        if (dailyRate == null) return 0;
        return days * dailyRate;
    }

    // getters/setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getRegistration() { return registration; }
    public void setRegistration(String registration) { this.registration = registration; }
    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }
    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    public String getLicensePlate() { return licensePlate; }
    public void setLicensePlate(String licensePlate) { this.licensePlate = licensePlate; }
    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }
    public Double getDailyRate() { return dailyRate; }
    public void setDailyRate(Double dailyRate) { this.dailyRate = dailyRate; }
    public List<RentalRequest> getRentalRequests() { return rentalRequests; }
    public void setRentalRequests(List<RentalRequest> rentalRequests) { this.rentalRequests = rentalRequests; }
}
