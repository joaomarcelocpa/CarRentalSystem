package com.example.backend.service;

import com.example.backend.model.CreditContract;
import com.example.backend.repository.CreditContractRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CreditContractService {
    private final CreditContractRepository repo;

    public CreditContractService(CreditContractRepository repo) { this.repo = repo; }

    public List<CreditContract> findAll() { return repo.findAll(); }
    public CreditContract findById(String id) { return repo.findById(id).orElse(null); }
    public CreditContract create(CreditContract cc) { cc.setId(UUID.randomUUID().toString()); return repo.save(cc); }
    public void delete(String id) { repo.deleteById(id); }
}
