package com.example.backend.controller;

import com.example.backend.dto.AutomobileCreateDTO;
import com.example.backend.dto.AutomobileResponseDTO;
import com.example.backend.service.AutomobileService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/automobiles")
public class AutomobileController {
    private final AutomobileService service;
    public AutomobileController(AutomobileService service) { this.service = service; }

    @GetMapping 
    public List<AutomobileResponseDTO> all() { 
        return service.findAll(); 
    }

    @GetMapping("/{id}")
    public ResponseEntity<AutomobileResponseDTO> get(@PathVariable String id) {
        AutomobileResponseDTO automobile = service.findById(id);
        return automobile == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(automobile);
    }

    @PostMapping 
    @PreAuthorize("hasAuthority('PERM_AUTOMOBILE_MANAGE')")
    public ResponseEntity<AutomobileResponseDTO> create(@Valid @RequestBody AutomobileCreateDTO createDTO) {
        AutomobileResponseDTO created = service.create(createDTO);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}") 
    @PreAuthorize("hasAuthority('PERM_AUTOMOBILE_MANAGE')")
    public ResponseEntity<AutomobileResponseDTO> update(@PathVariable String id, @Valid @RequestBody AutomobileCreateDTO updateDTO) {
        AutomobileResponseDTO updated = service.update(id, updateDTO);
        return updated == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}") 
    @PreAuthorize("hasAuthority('PERM_AUTOMOBILE_MANAGE')")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/agent/permissions")
    @PreAuthorize("hasAuthority('PERM_AUTOMOBILE_MANAGE')")
    public ResponseEntity<Map<String, Object>> checkAgentPermissions(Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        response.put("canManageAutomobiles", true);
        response.put("agentUsername", authentication.getName());
        response.put("authorities", authentication.getAuthorities());
        return ResponseEntity.ok(response);
    }
}
