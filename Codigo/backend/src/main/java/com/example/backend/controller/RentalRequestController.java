package com.example.backend.controller;

import com.example.backend.model.RentalRequest;
import com.example.backend.model.RequestStatus;
import com.example.backend.repository.RentalRequestRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/requests")
public class RentalRequestController {
    private final RentalRequestRepository repo;

    public RentalRequestController(RentalRequestRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public List<RentalRequest> all() { return repo.findAll(); }

    @GetMapping("/{id}")
    public RentalRequest get(@PathVariable String id) { return repo.findById(id).orElse(null); }

    @PostMapping
    public RentalRequest create(@RequestBody RentalRequest r) {
        if (r.getId() == null || r.getId().isBlank()) r.setId(UUID.randomUUID().toString());
        if (r.getStatus() == null) r.setStatus(RequestStatus.CREATED);
        return repo.save(r);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) { repo.deleteById(id); }

    // mudar status via PATCH
    @PatchMapping("/{id}/status")
    public RentalRequest changeStatus(@PathVariable String id, @RequestParam RequestStatus status) {
        return repo.findById(id).map(existing -> {
            existing.setStatus(status);
            return repo.save(existing);
        }).orElse(null);
    }
}
