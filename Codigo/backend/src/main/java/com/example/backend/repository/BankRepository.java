package com.example.backend.repository;

import com.example.backend.model.Bank;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BankRepository extends JpaRepository<Bank, String> {
    Optional<Bank> findByEmail(String email);
    Optional<Bank> findByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
}
