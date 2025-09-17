package com.example.backend.controller;

import com.example.backend.model.CreditContract;
import com.example.backend.service.CreditContractService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/credit-contracts")
public class CreditContractController {
    private final CreditContractService service;
    public CreditContractController(CreditContractService service) { this.service = service; }

    @PostMapping public ResponseEntity<CreditContract> create(@Valid @RequestBody CreditContract c) {
        return ResponseEntity.ok(service.create(c));
    }
}
