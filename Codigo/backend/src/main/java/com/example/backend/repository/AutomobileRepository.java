package com.example.backend.repository;

import com.example.backend.model.Automobile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AutomobileRepository extends JpaRepository<Automobile, String> {
    List<Automobile> findByCreatedByAgentUsername(String username);
}