package com.example.backend.repository;

import com.example.backend.model.Automobile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AutomobileRepository extends JpaRepository<Automobile, String> {}
