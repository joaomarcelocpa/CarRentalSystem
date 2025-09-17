package com.example.backend.service;

import com.example.backend.model.Income;
import com.example.backend.repository.IncomeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class IncomeService {
    private final IncomeRepository repo;

    public IncomeService(IncomeRepository repo) { this.repo = repo; }

    public List<Income> findAll() { return repo.findAll(); }
    public Income findById(String id) { return repo.findById(id).orElse(null); }
    public Income create(Income i) { i.setId(UUID.randomUUID().toString()); return repo.save(i); }
    public void delete(String id) { repo.deleteById(id); }
}
