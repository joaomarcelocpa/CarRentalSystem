package com.example.backend.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.backend.exception.IncomeLimitExceededException;
import com.example.backend.model.Income;
import com.example.backend.repository.IncomeRepository;

@Service
public class IncomeService {
    private final IncomeRepository repo;

    public IncomeService(IncomeRepository repo) { this.repo = repo; }

    public List<Income> findAll() { return repo.findAll(); }
    public Income findById(String id) { return repo.findById(id).orElse(null); }
    public Income create(Income i) {
        if (i.getCustomer() != null) {
            long count = repo.findAll().stream()
                .filter(income -> income.getCustomer() != null && income.getCustomer().getId().equals(i.getCustomer().getId()))
                .count();
            if (count >= 3) {
                throw new IncomeLimitExceededException("O usuário só pode cadastrar até 3 rendas.");
            }
        }
        i.setId(UUID.randomUUID().toString());
        return repo.save(i);
    }
    public void delete(String id) { repo.deleteById(id); }
}
