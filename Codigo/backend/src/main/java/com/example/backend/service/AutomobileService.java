package com.example.backend.service;

import com.example.backend.model.Automobile;
import com.example.backend.repository.AutomobileRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class AutomobileService {
    private final AutomobileRepository repo;

    public AutomobileService(AutomobileRepository repo) { this.repo = repo; }

    public List<Automobile> findAll() { return repo.findAll(); }
    public Automobile findById(String id) { return repo.findById(id).orElse(null); }
    public Automobile create(Automobile a) {
        a.setId(UUID.randomUUID().toString());
        if (a.getDailyRate() == null) a.setDailyRate(0.0);
        a.setAvailable(true);
        return repo.save(a);
    }
    public Automobile update(String id, Automobile updated) {
        return repo.findById(id).map(existing -> {
            existing.setBrand(updated.getBrand());
            existing.setModel(updated.getModel());
            existing.setDailyRate(updated.getDailyRate());
            existing.setAvailable(updated.isAvailable());
            return repo.save(existing);
        }).orElse(null);
    }
    public void delete(String id) { repo.deleteById(id); }
}
