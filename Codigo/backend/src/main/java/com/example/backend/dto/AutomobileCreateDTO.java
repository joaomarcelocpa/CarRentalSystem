package com.example.backend.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class AutomobileCreateDTO {
    @NotBlank(message = "Placa é obrigatória")
    private String licensePlate;
    
    @NotBlank(message = "Marca é obrigatória")
    private String brand;
    
    @NotBlank(message = "Modelo é obrigatória")
    private String model;
    
    @NotNull(message = "Ano é obrigatório")
    @Min(value = 1900, message = "Ano deve ser maior que 1900")
    private Integer year;
    
    private String registration;
    
    @NotNull(message = "Taxa diária é obrigatória")
    @Min(value = 0, message = "Taxa diária deve ser maior ou igual a 0")
    private Double dailyRate;

    public AutomobileCreateDTO() {}

    public String getLicensePlate() { return licensePlate; }
    public void setLicensePlate(String licensePlate) { this.licensePlate = licensePlate; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }

    public String getRegistration() { return registration; }
    public void setRegistration(String registration) { this.registration = registration; }

    public Double getDailyRate() { return dailyRate; }
    public void setDailyRate(Double dailyRate) { this.dailyRate = dailyRate; }
}
