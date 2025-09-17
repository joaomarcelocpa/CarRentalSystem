package com.example.backend.repository;

import com.example.backend.model.Agent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AgentRepository extends JpaRepository<Agent, String> {}
