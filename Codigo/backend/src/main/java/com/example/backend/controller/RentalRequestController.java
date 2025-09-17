package com.example.backend.controller;

import com.example.backend.model.RentalRequest;
import com.example.backend.model.enums.RequestStatus;
import com.example.backend.service.RentalRequestService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/requests")
public class RentalRequestController {
    private final RentalRequestService service;
    public RentalRequestController(RentalRequestService service) { this.service = service; }

    @GetMapping public List<RentalRequest> all() { return service.findAll(); }

    @GetMapping("/{id}") public ResponseEntity<RentalRequest> get(@PathVariable String id) {
        RentalRequest r = service.findById(id);
        return r == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(r);
    }

    @PostMapping public ResponseEntity<RentalRequest> create(@Valid @RequestBody RentalRequest r) {
        return ResponseEntity.ok(service.create(r));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<RentalRequest> changeStatus(@PathVariable String id, @RequestParam RequestStatus status) {
        RentalRequest updated = service.changeStatus(id, status);
        return updated == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}") public ResponseEntity<Void> delete(@PathVariable String id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
