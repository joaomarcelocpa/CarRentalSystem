package com.example.backend.service;

import com.example.backend.model.RentalContract;
import com.example.backend.repository.RentalContractRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class RentalContractService {
    private final RentalContractRepository repo;

    public RentalContractService(RentalContractRepository repo) { this.repo = repo; }

    public List<RentalContract> findAll() { return repo.findAll(); }
    public RentalContract findById(String id) { return repo.findById(id).orElse(null); }
    public RentalContract create(RentalContract rc) { rc.setId(UUID.randomUUID().toString()); return repo.save(rc); }
    public void delete(String id) { repo.deleteById(id); }
}
