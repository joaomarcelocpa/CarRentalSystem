package com.example.backend.controller;

import com.example.backend.dto.*;
import com.example.backend.model.RentalContract;
import com.example.backend.service.ContractWorkflowService;
import com.example.backend.service.ContractAnalysisService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/contract-workflow")
public class ContractWorkflowController {

    private final ContractWorkflowService workflowService;
    private final ContractAnalysisService analysisService;

    public ContractWorkflowController(ContractWorkflowService workflowService,
                                      ContractAnalysisService analysisService) {
        this.workflowService = workflowService;
        this.analysisService = analysisService;
    }

    @PostMapping("/process-request/{requestId}")
    @PreAuthorize("hasRole('AGENT_COMPANY') or hasRole('AGENT_BANK')")
    public ResponseEntity<RentalContract> processRentalRequest(
            @PathVariable String requestId,
            @RequestParam String agentId) {
        try {
            RentalContract contract = workflowService.processRentalRequestToContract(requestId, agentId);
            return ResponseEntity.ok(contract);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/process-request-with-credit/{requestId}")
    @PreAuthorize("hasRole('AGENT_BANK')")
    public ResponseEntity<RentalContract> processRentalRequestWithCredit(
            @PathVariable String requestId,
            @Valid @RequestBody CreditGrantDTO creditData) {
        try {
            RentalContract contract = workflowService.processRentalRequestWithCredit(
                    requestId,
                    creditData.getBankId(),
                    creditData.getInterestRate(),
                    creditData.getTermInMonths()
            );
            return ResponseEntity.ok(contract);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/finalize-contract/{contractId}")
    @PreAuthorize("hasRole('AGENT_COMPANY') or hasRole('AGENT_BANK')")
    public ResponseEntity<Void> finalizeContract(
            @PathVariable String contractId,
            @Valid @RequestBody ContractFinalizationDTO finalizationData) {
        try {
            workflowService.finalizeRentalContract(contractId, finalizationData.getFinalizationReason());
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/process-renewals")
    @PreAuthorize("hasRole('AGENT_COMPANY') or hasRole('AGENT_BANK')")
    public ResponseEntity<List<RentalContract>> processAutomaticRenewals() {
        List<RentalContract> renewedContracts = workflowService.processContractRenewals();
        return ResponseEntity.ok(renewedContracts);
    }

    @PostMapping("/liquidate-credit/{creditId}")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('AGENT_BANK')")
    public ResponseEntity<Void> liquidateCredit(
            @PathVariable String creditId,
            @RequestBody Map<String, Double> request) {
        try {
            Double liquidationAmount = request.get("liquidationAmount");
            workflowService.processEarlyLiquidation(creditId, liquidationAmount);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}