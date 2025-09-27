package com.example.backend.service;

import com.example.backend.dto.AutomobileCreateDTO;
import com.example.backend.dto.AutomobileResponseDTO;
import com.example.backend.model.Automobile;
import com.example.backend.repository.AutomobileRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AutomobileService {
    private final AutomobileRepository repo;

    public AutomobileService(AutomobileRepository repo) { this.repo = repo; }

    public List<AutomobileResponseDTO> findAll() { 
        return repo.findAll().stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }
    
    public AutomobileResponseDTO findById(String id) { 
        return repo.findById(id)
                .map(this::convertToResponseDTO)
                .orElse(null);
    }
    
    public AutomobileResponseDTO create(AutomobileCreateDTO createDTO) {
        Automobile automobile = new Automobile();
        automobile.setId(UUID.randomUUID().toString());
        automobile.setLicensePlate(createDTO.getLicensePlate());
        automobile.setBrand(createDTO.getBrand());
        automobile.setModel(createDTO.getModel());
        automobile.setYear(createDTO.getYear());
        automobile.setRegistration(createDTO.getRegistration());
        automobile.setDailyRate(createDTO.getDailyRate());
        automobile.setAvailable(true);
        automobile.setCreatedAt(LocalDate.now());
        
        return convertToResponseDTO(repo.save(automobile));
    }
    
    public AutomobileResponseDTO update(String id, AutomobileCreateDTO updateDTO) {
        return repo.findById(id).map(existing -> {
            existing.setLicensePlate(updateDTO.getLicensePlate());
            existing.setBrand(updateDTO.getBrand());
            existing.setModel(updateDTO.getModel());
            existing.setYear(updateDTO.getYear());
            existing.setRegistration(updateDTO.getRegistration());
            existing.setDailyRate(updateDTO.getDailyRate());
            return convertToResponseDTO(repo.save(existing));
        }).orElse(null);
    }
    public void delete(String id) { repo.deleteById(id); }
    
    private AutomobileResponseDTO convertToResponseDTO(Automobile automobile) {
        AutomobileResponseDTO dto = new AutomobileResponseDTO();
        dto.setId(automobile.getId());
        dto.setLicensePlate(automobile.getLicensePlate());
        dto.setBrand(automobile.getBrand());
        dto.setModel(automobile.getModel());
        dto.setYear(automobile.getYear());
        dto.setRegistration(automobile.getRegistration());
        dto.setAvailable(automobile.isAvailable());
        dto.setDailyRate(automobile.getDailyRate());
        dto.setCreatedAt(automobile.getCreatedAt());
        return dto;
    }
}
