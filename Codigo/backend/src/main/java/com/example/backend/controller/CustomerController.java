package com.example.backend.controller;

import com.example.backend.dto.*;
import com.example.backend.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {
    private final CustomerService service;
    public CustomerController(CustomerService service) { this.service = service; }

    @GetMapping
    public List<CustomerResponseDTO> all() {
        return service.findAllAsDTO();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponseDTO> get(@PathVariable String id) {
        CustomerResponseDTO dto = service.findByIdAsDTO(id);
        return dto == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(dto);
    }

    @PostMapping
    public ResponseEntity<CustomerResponseDTO> create(@Valid @RequestBody CustomerCreateDTO dto) {
        return ResponseEntity.ok(service.createFromDTO(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomerResponseDTO> update(@PathVariable String id, @Valid @RequestBody CustomerCreateDTO dto) {
        CustomerResponseDTO updated = service.updateFromDTO(id, dto);
        return updated == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}