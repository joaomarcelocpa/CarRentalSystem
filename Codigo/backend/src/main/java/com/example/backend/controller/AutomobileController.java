package com.example.backend.controller;

import com.example.backend.model.Automobile;
import com.example.backend.service.AutomobileService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/automobiles")
public class AutomobileController {
    private final AutomobileService service;
    public AutomobileController(AutomobileService service) { this.service = service; }

    @GetMapping public List<Automobile> all() { return service.findAll(); }

    @GetMapping("/{id}")
    public ResponseEntity<Automobile> get(@PathVariable String id) {
        Automobile a = service.findById(id);
        return a == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(a);
    }

    @PostMapping public ResponseEntity<Automobile> create(@Valid @RequestBody Automobile a) {
        return ResponseEntity.ok(service.create(a));
    }

    @PutMapping("/{id}") public ResponseEntity<Automobile> update(@PathVariable String id, @Valid @RequestBody Automobile a) {
        Automobile updated = service.update(id, a);
        return updated == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}") public ResponseEntity<Void> delete(@PathVariable String id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
