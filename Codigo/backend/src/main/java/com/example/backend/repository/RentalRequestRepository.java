package com.example.backend.repository;

import com.example.backend.model.RentalRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RentalRequestRepository extends JpaRepository<RentalRequest, String> {}
