package com.example.backend.repository;

import com.example.backend.model.CompanyAgent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CompanyAgentRepository extends JpaRepository<CompanyAgent, String> {
    Optional<CompanyAgent> findByEmail(String email);
    Optional<CompanyAgent> findByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
}
