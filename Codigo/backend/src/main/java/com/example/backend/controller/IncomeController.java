package com.example.backend.controller;

import com.example.backend.model.Income;
import com.example.backend.service.IncomeService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/incomes")
public class IncomeController {
    private final IncomeService service;
    public IncomeController(IncomeService service) { this.service = service; }

    @PostMapping public ResponseEntity<Income> create(@Valid @RequestBody Income i) {
        return ResponseEntity.ok(service.create(i));
    }
}
