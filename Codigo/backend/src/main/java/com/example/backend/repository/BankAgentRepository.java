package com.example.backend.repository;

import com.example.backend.model.BankAgent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BankAgentRepository extends JpaRepository<BankAgent, String> {
    Optional<BankAgent> findByEmail(String email);
    Optional<BankAgent> findByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
}
