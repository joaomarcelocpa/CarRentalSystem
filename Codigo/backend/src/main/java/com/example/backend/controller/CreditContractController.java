package com.example.backend.controller;

import com.example.backend.dto.*;
import com.example.backend.model.CreditContract;
import com.example.backend.model.Customer;
import com.example.backend.service.CreditContractService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/credit-contracts")
public class CreditContractController {

    private final CreditContractService service;

    public CreditContractController(CreditContractService service) {
        this.service = service;
    }

    @GetMapping
    @PreAuthorize("hasRole('AGENT_BANK')")
    public ResponseEntity<List<CreditContractResponseDTO>> getAllCredits() {
        List<CreditContract> credits = service.findAll();
        List<CreditContractResponseDTO> response = credits.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('AGENT_BANK')")
    public ResponseEntity<CreditContractResponseDTO> getCredit(@PathVariable String id) {
        return service.findById(id)
                .map(this::convertToResponseDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/active")
    @PreAuthorize("hasRole('AGENT_BANK')")
    public ResponseEntity<List<CreditContractResponseDTO>> getActiveCredits() {
        List<CreditContract> credits = service.findActiveCredits();
        List<CreditContractResponseDTO> response = credits.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/grant")
    @PreAuthorize("hasRole('AGENT_BANK')")
    public ResponseEntity<CreditContractResponseDTO> grantCredit(@Valid @RequestBody CreditGrantDTO grantDTO) {
        try {
            CreditContract credit = service.grantCredit(
                    grantDTO.getRequestId(),
                    grantDTO.getBankId(),
                    grantDTO.getInterestRate(),
                    grantDTO.getTermInMonths()
            );
            return ResponseEntity.ok(convertToResponseDTO(credit));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}/calculations")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('AGENT_BANK')")
    public ResponseEntity<Map<String, Double>> getCreditCalculations(@PathVariable String id) {
        try {
            double monthlyInstallment = service.calculateMonthlyInstallment(id);
            double totalInterest = service.calculateTotalInterest(id);

            return ResponseEntity.ok(Map.of(
                    "monthlyInstallment", monthlyInstallment,
                    "totalInterest", totalInterest
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{id}/payment")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('AGENT_BANK')")
    public ResponseEntity<CreditContractResponseDTO> payInstallment(
            @PathVariable String id,
            @Valid @RequestBody CreditPaymentDTO paymentDTO) {
        try {
            CreditContract updated = service.payInstallment(id, paymentDTO.getAmount());
            return ResponseEntity.ok(convertToResponseDTO(updated));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}/liquidate")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('AGENT_BANK')")
    public ResponseEntity<Void> liquidateCredit(@PathVariable String id) {
        try {
            service.liquidateCredit(id);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/evaluate-creditworthiness")
    @PreAuthorize("hasRole('AGENT_BANK')")
    public ResponseEntity<Map<String, Boolean>> evaluateCreditworthiness(
            @RequestBody Map<String, Object> request) {
        try {
            Customer customer = new Customer();
            Double requestedAmount = Double.valueOf(request.get("requestedAmount").toString());

            boolean approved = service.evaluateCreditworthiness(customer, requestedAmount);
            return ResponseEntity.ok(Map.of("approved", approved));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('AGENT_BANK')")
    public ResponseEntity<Void> deleteCredit(@PathVariable String id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    // Método auxiliar para conversão
    private CreditContractResponseDTO convertToResponseDTO(CreditContract credit) {
        CreditContractResponseDTO dto = new CreditContractResponseDTO();
        dto.setId(credit.getId());
        dto.setValue(credit.getValue());
        dto.setInterestRate(credit.getInterestRate());
        dto.setTerm(credit.getTerm());
        dto.setGrantDate(credit.getGrantDate());
        dto.setStatus(credit.getStatus());
        dto.setLastPaymentDate(credit.getLastPaymentDate());
        dto.setLiquidationDate(credit.getLiquidationDate());
        dto.setMonthlyInstallment(credit.calculateInstallment());
        dto.setTotalPayable(credit.calculateTotalPayable());
        dto.setTotalInterest(credit.calculateTotalInterest());
        dto.setRemainingMonths(credit.getRemainingMonths());
        dto.setOverdue(credit.isOverdue());

        if (credit.getRentalRequest() != null && credit.getRentalRequest().getCustomer() != null) {
            CustomerSummaryDTO customerDTO = new CustomerSummaryDTO();
            customerDTO.setId(credit.getRentalRequest().getCustomer().getId());
            customerDTO.setName(credit.getRentalRequest().getCustomer().getName());
            customerDTO.setEmailContact(credit.getRentalRequest().getCustomer().getEmailContact());
            dto.setCustomer(customerDTO);
        }

        if (credit.getGrantingBank() != null) {
            BankSummaryDTO bankDTO = new BankSummaryDTO();
            bankDTO.setId(credit.getGrantingBank().getId());
            bankDTO.setBankCode(credit.getGrantingBank().getBankCode());
            bankDTO.setUsername(credit.getGrantingBank().getUsername());
            dto.setGrantingBank(bankDTO);
        }

        return dto;
    }
}