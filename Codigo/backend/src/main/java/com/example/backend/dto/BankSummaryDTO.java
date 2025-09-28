package com.example.backend.dto;

public class BankSummaryDTO {
    
    private String id;
    private String bankCode;
    private String username;
    
    public BankSummaryDTO() {}
    
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getBankCode() { return bankCode; }
    public void setBankCode(String bankCode) { this.bankCode = bankCode; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
}