package com.example.backend.controller;
import com.example.backend.model.Automobile;
import com.example.backend.repository.AutomobileRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/automobiles")
public class AutomobileController {
    private final AutomobileRepository repo;

    public AutomobileController(AutomobileRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public List<Automobile> all() { return repo.findAll(); }

    @GetMapping("/{id}")
    public Automobile get(@PathVariable String id) { return repo.findById(id).orElse(null); }

    @PostMapping
    public Automobile create(@RequestBody Automobile a) {
        if (a.getId() == null || a.getId().isBlank()) a.setId(UUID.randomUUID().toString());
        return repo.save(a);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) { repo.deleteById(id); }
}