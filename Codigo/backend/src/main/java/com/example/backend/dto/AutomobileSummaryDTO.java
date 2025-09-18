package com.example.backend.dto;

public class AutomobileSummaryDTO {
    private String id;
    private String brand;
    private String model;
    private int year;
    private Double dailyRate;

    public AutomobileSummaryDTO() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }
    public Double getDailyRate() { return dailyRate; }
    public void setDailyRate(Double dailyRate) { this.dailyRate = dailyRate; }
}