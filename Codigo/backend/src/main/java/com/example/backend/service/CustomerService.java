package com.example.backend.service;

import com.example.backend.model.Customer;
import com.example.backend.repository.CustomerRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CustomerService {
    private final CustomerRepository repo;
    public CustomerService(CustomerRepository repo) { this.repo = repo; }

    public List<Customer> findAll() { return repo.findAll(); }
    public Customer findById(String id) { return repo.findById(id).orElse(null); }
    public Customer create(Customer c) {
        c.setId(UUID.randomUUID().toString());
        return repo.save(c);
    }
    public Customer update(String id, Customer updated) {
        return repo.findById(id).map(existing -> {
            existing.setName(updated.getName());
            existing.setEmailContact(updated.getEmailContact());
            existing.setAddress(updated.getAddress());
            existing.setProfession(updated.getProfession());
            return repo.save(existing);
        }).orElse(null);
    }
    public void delete(String id) { repo.deleteById(id); }
}
