package com.example.backend.repository;

import com.example.backend.model.EmployerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployerEntityRepository extends JpaRepository<EmployerEntity, String> {}
