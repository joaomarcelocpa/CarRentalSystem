package com.example.backend.controller;

import com.example.backend.model.Customer;
import com.example.backend.repository.CustomerRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/customers")
public class CustomerController {
    private final CustomerRepository repo;

    public CustomerController(CustomerRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public List<Customer> all() {
        return repo.findAll();
    }

    @GetMapping("/{id}")
    public Customer get(@PathVariable String id) {
        return repo.findById(id).orElse(null);
    }

    @PostMapping
    public Customer create(@RequestBody Customer c) {
        if (c.getId() == null || c.getId().isBlank()) {
            c.setId(UUID.randomUUID().toString());
        }
        return repo.save(c);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) { repo.deleteById(id); }
}
