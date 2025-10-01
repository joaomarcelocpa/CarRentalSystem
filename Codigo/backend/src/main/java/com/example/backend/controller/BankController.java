package com.example.backend.controller;

import com.example.backend.model.Bank;
import com.example.backend.service.BankService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/banks")
public class BankController {
    private final BankService service;

    public BankController(BankService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Bank> create(@Valid @RequestBody Bank b) {
        return ResponseEntity.ok(service.create(b));
    }
}