package com.example.backend.controller;

import com.example.backend.model.Customer;
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

    @GetMapping public List<Customer> all() { return service.findAll(); }

    @GetMapping("/{id}")
    public ResponseEntity<Customer> get(@PathVariable String id) {
        Customer c = service.findById(id);
        return c == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(c);
    }

    @PostMapping public ResponseEntity<Customer> create(@Valid @RequestBody Customer c) {
        return ResponseEntity.ok(service.create(c));
    }

    @PutMapping("/{id}") public ResponseEntity<Customer> update(@PathVariable String id, @Valid @RequestBody Customer c) {
        Customer updated = service.update(id, c);
        return updated == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}") public ResponseEntity<Void> delete(@PathVariable String id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
