package com.example.backend.repository;

import com.example.backend.model.Income;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IncomeRepository extends JpaRepository<Income, String> {}
