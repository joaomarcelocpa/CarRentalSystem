package com.example.backend.controller;

import com.example.backend.dto.*;
import com.example.backend.model.RentalContract;
import com.example.backend.service.RentalContractService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/rental-contracts")
public class RentalContractController {

    private final RentalContractService service;

    public RentalContractController(RentalContractService service) {
        this.service = service;
    }

    @GetMapping
    @PreAuthorize("hasRole('AGENT_COMPANY') or hasRole('AGENT_BANK')")
    public ResponseEntity<List<RentalContractResponseDTO>> getAllContracts() {
        List<RentalContract> contracts = service.findAll();
        List<RentalContractResponseDTO> response = contracts.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('AGENT_COMPANY') or hasRole('AGENT_BANK')")
    public ResponseEntity<RentalContractResponseDTO> getContract(@PathVariable String id) {
        return service.findById(id)
                .map(this::convertToResponseDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/active")
    @PreAuthorize("hasRole('AGENT_COMPANY') or hasRole('AGENT_BANK')")
    public ResponseEntity<List<RentalContractResponseDTO>> getActiveContracts() {
        List<RentalContract> contracts = service.findActiveContracts();
        List<RentalContractResponseDTO> response = contracts.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/expiring")
    @PreAuthorize("hasRole('AGENT_COMPANY') or hasRole('AGENT_BANK')")
    public ResponseEntity<List<RentalContractResponseDTO>> getExpiringContracts(
            @RequestParam(defaultValue = "30") int daysAhead) {
        List<RentalContract> contracts = service.findExpiringContracts(daysAhead);
        List<RentalContractResponseDTO> response = contracts.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/from-request/{requestId}")
    @PreAuthorize("hasRole('AGENT_COMPANY') or hasRole('AGENT_BANK')")
    public ResponseEntity<RentalContractResponseDTO> createFromRequest(@PathVariable String requestId) {
        try {
            RentalContract contract = service.createFromApprovedRequest(requestId);
            return ResponseEntity.ok(convertToResponseDTO(contract));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}/renew")
    @PreAuthorize("hasRole('AGENT_COMPANY') or hasRole('AGENT_BANK')")
    public ResponseEntity<RentalContractResponseDTO> renewContract(
            @PathVariable String id,
            @Valid @RequestBody ContractRenewalDTO renewalDTO) {
        try {
            RentalContract renewed = service.renewContract(id, renewalDTO.getNewEndDate());
            return ResponseEntity.ok(convertToResponseDTO(renewed));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}/finalize")
    @PreAuthorize("hasRole('AGENT_COMPANY') or hasRole('AGENT_BANK')")
    public ResponseEntity<Void> finalizeContract(
            @PathVariable String id,
            @Valid @RequestBody ContractFinalizationDTO finalizationDTO) {
        try {
            service.finalizeContract(id);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}/value")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('AGENT_COMPANY') or hasRole('AGENT_BANK')")
    public ResponseEntity<Map<String, Double>> getContractValue(@PathVariable String id) {
        try {
            double totalValue = service.calculateTotalContractValue(id);
            return ResponseEntity.ok(Map.of("totalValue", totalValue));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}/status")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('AGENT_COMPANY') or hasRole('AGENT_BANK')")
    public ResponseEntity<Map<String, Boolean>> getContractStatus(@PathVariable String id) {
        boolean isActive = service.isContractActive(id);
        return ResponseEntity.ok(Map.of("isActive", isActive));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('AGENT_BANK')")
    public ResponseEntity<Void> deleteContract(@PathVariable String id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    // Método auxiliar para conversão
    private RentalContractResponseDTO convertToResponseDTO(RentalContract contract) {
        RentalContractResponseDTO dto = new RentalContractResponseDTO();
        dto.setId(contract.getId());
        dto.setStartDate(contract.getStartDate());
        dto.setEndDate(contract.getEndDate());
        dto.setValue(contract.getValue());
        dto.setSigningDate(contract.getSigningDate());
        dto.setStatus(contract.getStatus());
        dto.setRenewalCount(contract.getRenewalCount());
        dto.setDurationInDays(contract.getDurationInDays());
        dto.setDaysRemaining(contract.getDaysRemaining());
        dto.setCurrentlyActive(contract.isCurrentlyActive());

        // Converter dados do cliente
        if (contract.getRentalRequest() != null && contract.getRentalRequest().getCustomer() != null) {
            CustomerSummaryDTO customerDTO = new CustomerSummaryDTO();
            customerDTO.setId(contract.getRentalRequest().getCustomer().getId());
            customerDTO.setName(contract.getRentalRequest().getCustomer().getName());
            customerDTO.setEmailContact(contract.getRentalRequest().getCustomer().getEmailContact());
            dto.setCustomer(customerDTO);
        }

        // Converter dados do automóvel
        if (contract.getRentalRequest() != null && contract.getRentalRequest().getAutomobile() != null) {
            AutomobileSummaryDTO autoDTO = new AutomobileSummaryDTO();
            autoDTO.setId(contract.getRentalRequest().getAutomobile().getId());
            autoDTO.setBrand(contract.getRentalRequest().getAutomobile().getBrand());
            autoDTO.setModel(contract.getRentalRequest().getAutomobile().getModel());
            autoDTO.setYear(contract.getRentalRequest().getAutomobile().getYear());
            autoDTO.setDailyRate(contract.getRentalRequest().getAutomobile().getDailyRate());
            dto.setAutomobile(autoDTO);
        }

        return dto;
    }
}