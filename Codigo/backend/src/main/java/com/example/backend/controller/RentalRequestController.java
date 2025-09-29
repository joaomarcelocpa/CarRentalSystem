package com.example.backend.controller;

import com.example.backend.dto.RentalRequestCreateDTO;
import com.example.backend.dto.RentalRequestResponseDTO;
import com.example.backend.dto.RentalRequestStatusUpdateDTO;
import com.example.backend.dto.RentalRequestUpdateDTO;
import com.example.backend.security.JwtTokenProvider;
import com.example.backend.service.RentalRequestService;
import jakarta.servlet.http.HttpServletRequest;
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
@RequestMapping("/api/rental-requests")
public class RentalRequestController {

    private final RentalRequestService rentalRequestService;
    private final JwtTokenProvider jwtTokenProvider;

    public RentalRequestController(RentalRequestService rentalRequestService,
                                   JwtTokenProvider jwtTokenProvider) {
        this.rentalRequestService = rentalRequestService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    // ==================== ENDPOINTS PARA CLIENTES ====================

    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<?> createRequest(
            @Valid @RequestBody RentalRequestCreateDTO dto,
            Authentication authentication) {
        try {
            String username = authentication.getName();
            RentalRequestResponseDTO response = rentalRequestService.createRequest(username, dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('AGENT_COMPANY') or hasRole('AGENT_BANK')")
    public ResponseEntity<?> getRequestById(
            @PathVariable String id,
            Authentication authentication) {
        try {
            RentalRequestResponseDTO response = rentalRequestService.findRequestById(id);

            String username = authentication.getName();
            boolean isCustomer = authentication.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_CUSTOMER"));

            if (isCustomer && !response.getCustomer().getName().equals(username)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                        Map.of("error", "Você não tem permissão para visualizar este pedido")
                );
            }

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/my-requests")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<List<RentalRequestResponseDTO>> getMyRequests(
            Authentication authentication) {
        String username = authentication.getName();
        List<RentalRequestResponseDTO> requests =
                rentalRequestService.findRequestsByCustomer(username);
        return ResponseEntity.ok(requests);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<?> updateRequest(
            @PathVariable String id,
            @Valid @RequestBody RentalRequestUpdateDTO dto,
            Authentication authentication) {
        try {
            String username = authentication.getName();
            RentalRequestResponseDTO response =
                    rentalRequestService.updateRequest(id, username, dto);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PatchMapping("/{id}/cancel")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<?> cancelRequest(
            @PathVariable String id,
            Authentication authentication) {
        try {
            String username = authentication.getName();
            RentalRequestResponseDTO response =
                    rentalRequestService.cancelRequest(id, username);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<?> deleteRequest(
            @PathVariable String id,
            Authentication authentication) {
        try {
            String username = authentication.getName();
            rentalRequestService.deleteRequest(id, username);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    @GetMapping("/pending")
    @PreAuthorize("hasRole('AGENT_COMPANY') or hasRole('AGENT_BANK')")
    public ResponseEntity<List<RentalRequestResponseDTO>> getPendingRequests() {
        List<RentalRequestResponseDTO> requests = rentalRequestService.findPendingRequests();
        return ResponseEntity.ok(requests);
    }

    @GetMapping("/agent/my-automobiles")
    @PreAuthorize("hasRole('AGENT_COMPANY')")
    public ResponseEntity<List<RentalRequestResponseDTO>> getRequestsForMyAutomobiles(
            Authentication authentication) {
        String username = authentication.getName();
        List<RentalRequestResponseDTO> requests =
                rentalRequestService.findRequestsForAgentAutomobiles(username);
        return ResponseEntity.ok(requests);
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('AGENT_COMPANY') or hasRole('AGENT_BANK')")
    public ResponseEntity<List<RentalRequestResponseDTO>> getAllRequests() {
        List<RentalRequestResponseDTO> requests = rentalRequestService.findAllRequests();
        return ResponseEntity.ok(requests);
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('AGENT_COMPANY') or hasRole('AGENT_BANK')")
    public ResponseEntity<?> updateRequestStatus(
            @PathVariable String id,
            @Valid @RequestBody RentalRequestStatusUpdateDTO dto,
            HttpServletRequest request) {
        try {
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            String token = authHeader.substring(7);
            String agentUsername = jwtTokenProvider.getUsernameFromToken(token);
            String agentId = jwtTokenProvider.getUserIdFromToken(token);

            RentalRequestResponseDTO response =
                    rentalRequestService.updateRequestStatus(id, agentUsername, agentId, dto);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/statistics")
    @PreAuthorize("hasRole('AGENT_COMPANY') or hasRole('AGENT_BANK')")
    public ResponseEntity<Map<String, Object>> getStatistics() {
        List<RentalRequestResponseDTO> allRequests = rentalRequestService.findAllRequests();

        Map<String, Object> stats = new HashMap<>();
        stats.put("total", allRequests.size());
        stats.put("pending", allRequests.stream()
                .filter(r -> r.getStatus().name().equals("PENDING")).count());
        stats.put("approved", allRequests.stream()
                .filter(r -> r.getStatus().name().equals("APPROVED")).count());
        stats.put("rejected", allRequests.stream()
                .filter(r -> r.getStatus().name().equals("REJECTED")).count());
        stats.put("active", allRequests.stream()
                .filter(r -> r.getStatus().name().equals("ACTIVE")).count());
        stats.put("completed", allRequests.stream()
                .filter(r -> r.getStatus().name().equals("COMPLETED")).count());
        stats.put("cancelled", allRequests.stream()
                .filter(r -> r.getStatus().name().equals("CANCELLED")).count());

        return ResponseEntity.ok(stats);
    }
}