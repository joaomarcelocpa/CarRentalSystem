package com.example.backend.controller;

import com.example.backend.dto.LoginRequestDTO;
import com.example.backend.dto.LoginResponseDTO;
import com.example.backend.dto.UserCreateDTO;
import com.example.backend.dto.UserResponseDTO;
import com.example.backend.service.AuthService;
import com.example.backend.security.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO loginRequest) {
        LoginResponseDTO response = authService.login(loginRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<LoginResponseDTO> register(@Valid @RequestBody UserCreateDTO userCreateDTO) {
        UserResponseDTO userResponse = authService.register(userCreateDTO);
        
        // Gerar token JWT para o usuário recém-registrado
        String token = jwtTokenProvider.generateTokenFromUsername(userResponse.getUsername());
        
        LoginResponseDTO response = new LoginResponseDTO();
        response.setToken(token);
        response.setUsername(userResponse.getUsername());
        response.setEmail(userResponse.getEmail());
        response.setRole(userResponse.getRole());
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('AGENT_COMPANY') or hasRole('AGENT_BANK')")
    public ResponseEntity<UserResponseDTO> getCurrentUser(HttpServletRequest request) {
        // Extrair token do header Authorization
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            String username = jwtTokenProvider.getUsernameFromToken(token);
            UserResponseDTO response = authService.getCurrentUser(username);
            return ResponseEntity.ok(response);
        }
        throw new RuntimeException("Token não encontrado");
    }
}
