package com.example.backend.controller;

import com.example.backend.dto.CreditContractCreateDTO;
import com.example.backend.dto.CreditContractResponseDTO;
import com.example.backend.dto.CreditContractUpdateDTO;
import com.example.backend.service.CreditContractService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/credit-contracts")
public class CreditContractController {

    private final CreditContractService creditContractService;

    public CreditContractController(CreditContractService creditContractService) {
        this.creditContractService = creditContractService;
    }

    @PostMapping
    @PreAuthorize("hasRole('AGENT_BANK')")
    public ResponseEntity<?> createOrUpdateCreditLimit(
            @Valid @RequestBody CreditContractCreateDTO dto,
            Authentication authentication) {
        try {
            String bankAgentUsername = authentication.getName();
            CreditContractResponseDTO response = creditContractService
                    .createOrUpdateCreditLimit(bankAgentUsername, dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping
    @PreAuthorize("hasRole('AGENT_BANK')")
    public ResponseEntity<List<CreditContractResponseDTO>> getAllCreditContracts(
            Authentication authentication) {
        String bankAgentUsername = authentication.getName();
        List<CreditContractResponseDTO> contracts = creditContractService
                .getAllCreditContractsByBankAgent(bankAgentUsername);
        return ResponseEntity.ok(contracts);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('AGENT_BANK')")
    public ResponseEntity<?> getCreditContractById(
            @PathVariable String id,
            Authentication authentication) {
        try {
            String bankAgentUsername = authentication.getName();
            CreditContractResponseDTO response = creditContractService
                    .getCreditContractById(id, bankAgentUsername);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('AGENT_BANK')")
    public ResponseEntity<?> updateCreditContract(
            @PathVariable String id,
            @Valid @RequestBody CreditContractUpdateDTO dto,
            Authentication authentication) {
        try {
            String bankAgentUsername = authentication.getName();
            CreditContractResponseDTO response = creditContractService
                    .updateCreditContract(id, bankAgentUsername, dto);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('AGENT_BANK')")
    public ResponseEntity<?> deleteCreditContract(
            @PathVariable String id,
            Authentication authentication) {
        try {
            String bankAgentUsername = authentication.getName();
            creditContractService.deleteCreditContract(id, bankAgentUsername);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/check-limit")
    @PreAuthorize("hasRole('AGENT_BANK')")
    public ResponseEntity<Map<String, Object>> checkCreditLimit(
            @RequestParam String customerUsername,
            @RequestParam Double amount,
            Authentication authentication) {
        String bankAgentUsername = authentication.getName();
        boolean hasLimit = creditContractService
                .hasAvailableCredit(customerUsername, bankAgentUsername, amount);

        Map<String, Object> response = new HashMap<>();
        response.put("hasAvailableCredit", hasLimit);
        response.put("customerUsername", customerUsername);
        response.put("amount", amount);

        return ResponseEntity.ok(response);
    }
}