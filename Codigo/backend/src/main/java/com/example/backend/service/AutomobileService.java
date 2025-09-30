package com.example.backend.service;

import com.example.backend.dto.AutomobileCreateDTO;
import com.example.backend.dto.AutomobileResponseDTO;
import com.example.backend.model.Automobile;
import com.example.backend.repository.AutomobileRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AutomobileService {
    private static final Logger logger = LoggerFactory.getLogger(AutomobileService.class);

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

    public List<AutomobileResponseDTO> findByCreatedByAgentUsername(String username) {
        logger.info("Buscando veículos do agente: {}", username);
        List<Automobile> automobiles = repo.findByCreatedByAgentUsername(username);
        logger.info("Encontrados {} veículos para o agente {}", automobiles.size(), username);

        return automobiles.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    public AutomobileResponseDTO create(AutomobileCreateDTO createDTO, String agentId, String agentUsername) {
        logger.info("Criando veículo - AgentId: {}, AgentUsername: {}", agentId, agentUsername);

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

        // CORRIGIDO: Garantir que os IDs do agente sejam salvos
        automobile.setCreatedByAgentId(agentId);
        automobile.setCreatedByAgentUsername(agentUsername);

        logger.info("Salvando veículo com dados: ID={}, AgentId={}, AgentUsername={}",
                automobile.getId(), automobile.getCreatedByAgentId(), automobile.getCreatedByAgentUsername());

        Automobile saved = repo.save(automobile);

        logger.info("Veículo salvo com sucesso - ID: {}, CreatedByAgentId: {}, CreatedByAgentUsername: {}",
                saved.getId(), saved.getCreatedByAgentId(), saved.getCreatedByAgentUsername());

        return convertToResponseDTO(saved);
    }

    public AutomobileResponseDTO update(String id, AutomobileCreateDTO updateDTO) {
        return repo.findById(id).map(existing -> {
            existing.setLicensePlate(updateDTO.getLicensePlate());
            existing.setBrand(updateDTO.getBrand());
            existing.setModel(updateDTO.getModel());
            existing.setYear(updateDTO.getYear());
            existing.setRegistration(updateDTO.getRegistration());
            existing.setDailyRate(updateDTO.getDailyRate());

            // NÃO alterar os campos de criação no update
            // existing.setCreatedByAgentId(...) - MANTER o original
            // existing.setCreatedByAgentUsername(...) - MANTER o original

            return convertToResponseDTO(repo.save(existing));
        }).orElse(null);
    }

    public void delete(String id) {
        repo.deleteById(id);
    }

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

        // IMPORTANTE: Incluir os campos do agente no DTO
        dto.setCreatedByAgentId(automobile.getCreatedByAgentId());
        dto.setCreatedByAgentUsername(automobile.getCreatedByAgentUsername());

        logger.debug("Convertendo automobile {} - AgentId: {}, AgentUsername: {}",
                automobile.getId(), automobile.getCreatedByAgentId(), automobile.getCreatedByAgentUsername());

        return dto;
    }
}