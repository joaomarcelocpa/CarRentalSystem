package com.example.backend.repository;

import com.example.backend.model.RentalContract;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RentalContractRepository extends JpaRepository<RentalContract, String> {}
