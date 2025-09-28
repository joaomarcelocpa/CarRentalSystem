package com.example.backend.controller;

import com.example.backend.service.ContractAnalysisService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/contract-analysis")
public class ContractAnalysisController {

    private final ContractAnalysisService analysisService;

    public ContractAnalysisController(ContractAnalysisService analysisService) {
        this.analysisService = analysisService;
    }

    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('AGENT_COMPANY') or hasRole('AGENT_BANK')")
    public ResponseEntity<Map<String, Object>> getContractDashboard() {
        Map<String, Object> dashboard = analysisService.generateContractDashboard();
        return ResponseEntity.ok(dashboard);
    }

    @GetMapping("/reports/monthly/{year}/{month}")
    @PreAuthorize("hasRole('AGENT_BANK')")
    public ResponseEntity<Map<String, Object>> getMonthlyReport(
            @PathVariable int year,
            @PathVariable int month) {
        Map<String, Object> report = analysisService.generateMonthlyReport(year, month);
        return ResponseEntity.ok(report);
    }

    @GetMapping("/credit-risk/{customerId}")
    @PreAuthorize("hasRole('AGENT_BANK')")
    public ResponseEntity<Map<String, Object>> analyzeCreditRisk(@PathVariable String customerId) {
        Map<String, Object> analysis = analysisService.analyzeCreditRisk(customerId);
        return ResponseEntity.ok(analysis);
    }

    @GetMapping("/revenue-projection")
    @PreAuthorize("hasRole('AGENT_BANK')")
    public ResponseEntity<Map<String, Object>> getRevenueProjection(
            @RequestParam(defaultValue = "12") int monthsAhead) {
        Map<String, Object> projection = analysisService.generateRevenueProjection(monthsAhead);
        return ResponseEntity.ok(projection);
    }

    @GetMapping("/automobile-performance")
    @PreAuthorize("hasRole('AGENT_COMPANY') or hasRole('AGENT_BANK')")
    public ResponseEntity<Map<String, Object>> getAutomobilePerformance() {
        Map<String, Object> analysis = analysisService.analyzeAutomobilePerformance();
        return ResponseEntity.ok(analysis);
    }
}