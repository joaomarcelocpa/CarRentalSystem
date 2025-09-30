package com.example.backend.service;

import com.example.backend.dto.*;
import com.example.backend.model.Automobile;
import com.example.backend.model.Customer;
import com.example.backend.model.RentalRequest;
import com.example.backend.model.enums.RequestStatus;
import com.example.backend.repository.AutomobileRepository;
import com.example.backend.repository.CustomerRepository;
import com.example.backend.repository.RentalRequestRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class RentalRequestService {

    private final RentalRequestRepository rentalRequestRepository;
    private final CustomerRepository customerRepository;
    private final AutomobileRepository automobileRepository;

    public RentalRequestService(
            RentalRequestRepository rentalRequestRepository,
            CustomerRepository customerRepository,
            AutomobileRepository automobileRepository) {
        this.rentalRequestRepository = rentalRequestRepository;
        this.customerRepository = customerRepository;
        this.automobileRepository = automobileRepository;
    }

    @Transactional
    public RentalRequestResponseDTO createRequest(String customerUsername, RentalRequestCreateDTO dto) {
        if (dto.getReturnDate().isBefore(dto.getPickupDate())) {
            throw new IllegalArgumentException("Data de devolução deve ser posterior à data de retirada");
        }

        if (dto.getPickupDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Data de retirada deve ser no presente ou futuro");
        }

        Customer customer = customerRepository.findByUsername(customerUsername)
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado"));

        Automobile automobile = automobileRepository.findById(dto.getAutomobileId())
                .orElseThrow(() -> new IllegalArgumentException("Automóvel não encontrado"));

        if (!automobile.isAvailable()) {
            throw new IllegalArgumentException("Automóvel não está disponível");
        }

        RentalRequest request = new RentalRequest();
        request.setId(UUID.randomUUID().toString());
        request.setCustomer(customer);
        request.setAutomobile(automobile);
        request.setPickupDate(dto.getPickupDate());
        request.setReturnDate(dto.getReturnDate());
        request.setObservations(dto.getObservations());
        request.setStatus(RequestStatus.PENDING);
        request.setCreatedAt(LocalDate.now());
        request.calculateTotalValue();

        Double credit_limit = customer.getCreditLimit();
        Double totalValue = request.getTotalValue();

        if (credit_limit == null || credit_limit == 0.0) {
            request.setStatus(RequestStatus.PENDING);
        } else if (totalValue > credit_limit) {
            request.setStatus(RequestStatus.REJECTED);
            request.setRejectionReason("Valor do pedido (R$ " + String.format("%.2f", totalValue) +
                    ") ultrapassa o limite de crédito aprovado (R$ " + String.format("%.2f", credit_limit) + ")");
            request.setProcessedByAgentUsername("SISTEMA");
            request.setProcessedByAgentId("AUTO");
            request.setProcessedAt(LocalDate.now());

            automobile.setAvailable(true);
        } else {
            request.setStatus(RequestStatus.PENDING);
        }

        if (request.getStatus() != RequestStatus.REJECTED) {
            automobile.setAvailable(false);
        }

        automobileRepository.save(automobile);
        RentalRequest savedRequest = rentalRequestRepository.save(request);

        return convertToResponseDTO(savedRequest);
    }


    @Transactional(readOnly = true)
    public List<RentalRequestResponseDTO> findAllRequests() {
        return rentalRequestRepository.findAll().stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public RentalRequestResponseDTO findRequestById(String id) {
        RentalRequest request = rentalRequestRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado"));
        return convertToResponseDTO(request);
    }

    @Transactional(readOnly = true)
    public List<RentalRequestResponseDTO> findPendingRequests() {
        return rentalRequestRepository.findAllPending().stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<RentalRequestResponseDTO> findRequestsByCustomer(String customerUsername) {
        return rentalRequestRepository.findByCustomerUsername(customerUsername).stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<RentalRequestResponseDTO> findRequestsForAgentAutomobiles(String agentUsername) {
        return rentalRequestRepository.findRequestsForAgentAutomobiles(agentUsername).stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public RentalRequestResponseDTO updateRequest(String id, String customerUsername, RentalRequestUpdateDTO dto) {
        RentalRequest request = rentalRequestRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado"));

        if (!request.getCustomer().getUsername().equals(customerUsername)) {
            throw new IllegalArgumentException("Você não tem permissão para modificar este pedido");
        }

        if (!request.canBeModified()) {
            throw new IllegalArgumentException("Pedido não pode ser modificado no status atual: " +
                    request.getStatus().getDescription());
        }

        if (dto.getPickupDate() != null) {
            if (dto.getPickupDate().isBefore(LocalDate.now())) {
                throw new IllegalArgumentException("Data de retirada deve ser no presente ou futuro");
            }
            request.setPickupDate(dto.getPickupDate());
        }

        if (dto.getReturnDate() != null) {
            request.setReturnDate(dto.getReturnDate());
        }

        if (request.getReturnDate().isBefore(request.getPickupDate())) {
            throw new IllegalArgumentException("Data de devolução deve ser posterior à data de retirada");
        }

        if (dto.getObservations() != null) {
            request.setObservations(dto.getObservations());
        }

        request.calculateTotalValue();
        RentalRequest updatedRequest = rentalRequestRepository.save(request);
        return convertToResponseDTO(updatedRequest);
    }

    @Transactional
    public RentalRequestResponseDTO updateRequestStatus(
            String id,
            String agentUsername,
            String agentId,
            RentalRequestStatusUpdateDTO dto) {

        RentalRequest request = rentalRequestRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado"));

        if (request.getStatus() == RequestStatus.COMPLETED) {
            throw new IllegalArgumentException("Pedido já foi concluído");
        }

        if (request.getStatus() == RequestStatus.CANCELLED) {
            throw new IllegalArgumentException("Pedido já foi cancelado");
        }

        request.changeStatus(dto.getStatus(), agentId, agentUsername);

        if (dto.getStatus() == RequestStatus.REJECTED && dto.getRejectionReason() != null) {
            request.setRejectionReason(dto.getRejectionReason());
        }

        Automobile automobile = request.getAutomobile();

        switch (dto.getStatus()) {
            case APPROVED:
            case UNDER_ANALYSIS:
            case ACTIVE:
                automobile.setAvailable(false);
                break;

            case REJECTED:
            case CANCELLED:
            case COMPLETED:
                automobile.setAvailable(true);
                break;

            default:
                break;
        }

        automobileRepository.save(automobile);

        RentalRequest updatedRequest = rentalRequestRepository.save(request);
        return convertToResponseDTO(updatedRequest);
    }

    @Transactional
    public RentalRequestResponseDTO cancelRequest(String id, String customerUsername) {
        RentalRequest request = rentalRequestRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado"));

        if (!request.getCustomer().getUsername().equals(customerUsername)) {
            throw new IllegalArgumentException("Você não tem permissão para cancelar este pedido");
        }

        if (!request.canBeCancelled()) {
            throw new IllegalArgumentException("Pedido não pode ser cancelado no status atual: " +
                    request.getStatus().getDescription());
        }

        request.setStatus(RequestStatus.CANCELLED);

        Automobile automobile = request.getAutomobile();
        automobile.setAvailable(true);
        automobileRepository.save(automobile);

        RentalRequest updatedRequest = rentalRequestRepository.save(request);
        return convertToResponseDTO(updatedRequest);
    }

    @Transactional
    public void deleteRequest(String id, String customerUsername) {
        RentalRequest request = rentalRequestRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado"));

        if (!request.getCustomer().getUsername().equals(customerUsername)) {
            throw new IllegalArgumentException("Você não tem permissão para deletar este pedido");
        }

        if (request.getStatus() != RequestStatus.PENDING) {
            throw new IllegalArgumentException("Apenas pedidos pendentes podem ser deletados");
        }

        Automobile automobile = request.getAutomobile();
        automobile.setAvailable(true);
        automobileRepository.save(automobile);

        rentalRequestRepository.delete(request);
    }

    private RentalRequestResponseDTO convertToResponseDTO(RentalRequest request) {
        RentalRequestResponseDTO dto = new RentalRequestResponseDTO();
        dto.setId(request.getId());
        dto.setPickupDate(request.getPickupDate());
        dto.setReturnDate(request.getReturnDate());
        dto.setStatus(request.getStatus());
        dto.setStatusDescription(request.getStatus().getDescription());
        dto.setCreatedAt(request.getCreatedAt());
        dto.setTotalValue(request.getTotalValue());
        dto.setRentalDays(request.getRentalDays());
        dto.setObservations(request.getObservations());
        dto.setProcessedByAgentId(request.getProcessedByAgentId());
        dto.setProcessedByAgentUsername(request.getProcessedByAgentUsername());
        dto.setProcessedAt(request.getProcessedAt());

        if (request.getCustomer() != null) {
            CustomerSummaryDTO customerDTO = new CustomerSummaryDTO();
            customerDTO.setId(request.getCustomer().getId());
            customerDTO.setName(request.getCustomer().getUsername());
            customerDTO.setEmailContact(request.getCustomer().getEmail());
            dto.setCustomer(customerDTO);
        }

        if (request.getAutomobile() != null) {
            AutomobileSummaryDTO automobileDTO = new AutomobileSummaryDTO();
            automobileDTO.setId(request.getAutomobile().getId());
            automobileDTO.setBrand(request.getAutomobile().getBrand());
            automobileDTO.setModel(request.getAutomobile().getModel());
            automobileDTO.setYear(request.getAutomobile().getYear());
            automobileDTO.setDailyRate(request.getAutomobile().getDailyRate());
            dto.setAutomobile(automobileDTO);
        }

        return dto;
    }
}