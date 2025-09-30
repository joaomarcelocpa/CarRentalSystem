package com.example.backend.controller;

import com.example.backend.dto.AutomobileCreateDTO;
import com.example.backend.dto.AutomobileResponseDTO;
import com.example.backend.security.JwtTokenProvider;
import com.example.backend.service.AutomobileService;
import com.example.backend.service.UserService;
import com.example.backend.dto.UserResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
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
    private static final Logger logger = LoggerFactory.getLogger(AutomobileController.class);

    private final AutomobileService service;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;

    public AutomobileController(AutomobileService service, UserService userService, JwtTokenProvider jwtTokenProvider) {
        this.service = service;
        this.userService = userService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @GetMapping
    public List<AutomobileResponseDTO> all() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<AutomobileResponseDTO> get(@PathVariable String id) {
        AutomobileResponseDTO automobile = service.findById(id);
        return automobile == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(automobile);
    }

    @GetMapping("/my-automobiles")
    @PreAuthorize("hasAuthority('PERM_AUTOMOBILE_MANAGE')")
    public ResponseEntity<List<AutomobileResponseDTO>> getMyAutomobiles(Authentication authentication) {
        String username = authentication.getName();
        logger.info("Buscando veículos para o agente: {}", username);

        List<AutomobileResponseDTO> automobiles = service.findByCreatedByAgentUsername(username);

        logger.info("Retornando {} veículos para o agente {}", automobiles.size(), username);
        return ResponseEntity.ok(automobiles);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('PERM_AUTOMOBILE_MANAGE')")
    public ResponseEntity<?> create(
            @Valid @RequestBody AutomobileCreateDTO createDTO,
            HttpServletRequest request) {

        logger.info("Recebendo requisição de criação de veículo");

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            logger.error("Token de autenticação não encontrado");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String token = authHeader.substring(7);

        // IMPORTANTE: Extrair userId e username do token
        String userId = jwtTokenProvider.getUserIdFromToken(token);
        String username = jwtTokenProvider.getUsernameFromToken(token);

        logger.info("Criando veículo - UserId extraído: {}, Username extraído: {}", userId, username);

        // VERIFICAÇÃO: Garantir que os dados foram extraídos corretamente
        if (userId == null || username == null) {
            logger.error("Falha ao extrair userId ou username do token");
            Map<String, String> error = new HashMap<>();
            error.put("message", "Erro ao processar token de autenticação");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }

        try {
            AutomobileResponseDTO created = service.create(createDTO, userId, username);

            logger.info("Veículo criado com sucesso - ID: {}, CreatedByAgentId: {}, CreatedByAgentUsername: {}",
                    created.getId(), created.getCreatedByAgentId(), created.getCreatedByAgentUsername());

            return ResponseEntity.ok(created);
        } catch (Exception e) {
            logger.error("Erro ao criar veículo", e);
            Map<String, String> error = new HashMap<>();
            error.put("message", "Erro ao criar veículo: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
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