package com.example.backend.controller;

import com.example.backend.model.RentalContract;
import com.example.backend.service.RentalContractService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/contracts")
public class RentalContractController {
    private final RentalContractService service;
    public RentalContractController(RentalContractService service) { this.service = service; }

    @PostMapping public ResponseEntity<RentalContract> create(@Valid @RequestBody RentalContract rc) {
        return ResponseEntity.ok(service.create(rc));
    }
}
