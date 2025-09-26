package com.example.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @GetMapping("/public")
    public ResponseEntity<Map<String, String>> publicEndpoint() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Este é um endpoint público");
        response.put("status", "success");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/customer")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<Map<String, String>> customerEndpoint() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Este endpoint é apenas para clientes");
        response.put("status", "success");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/agent")
    @PreAuthorize("hasRole('AGENT_COMPANY') or hasRole('AGENT_BANK')")
    public ResponseEntity<Map<String, String>> agentEndpoint() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Este endpoint é apenas para agentes");
        response.put("status", "success");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('AGENT_BANK')")
    public ResponseEntity<Map<String, String>> adminEndpoint() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Este endpoint é apenas para agentes bancários");
        response.put("status", "success");
        return ResponseEntity.ok(response);
    }
}
