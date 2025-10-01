package com.example.backend.service;

import com.example.backend.dto.*;
import com.example.backend.model.Automobile;
import com.example.backend.model.Customer;
import com.example.backend.model.RentalRequest;
import com.example.backend.model.enums.RequestStatus;
import com.example.backend.model.enums.UserRole;
import com.example.backend.repository.AutomobileRepository;
import com.example.backend.repository.CustomerRepository;
import com.example.backend.repository.RentalRequestRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class RentalRequestService {

    private static final Logger logger = LoggerFactory.getLogger(RentalRequestService.class);

    private final RentalRequestRepository rentalRequestRepository;
    private final CustomerRepository customerRepository;
    private final AutomobileRepository automobileRepository;
    private final CreditContractService creditContractService;
    private final UserService userService;

    public RentalRequestService(
            RentalRequestRepository rentalRequestRepository,
            CustomerRepository customerRepository,
            AutomobileRepository automobileRepository,
            CreditContractService creditContractService,
            UserService userService) {
        this.rentalRequestRepository = rentalRequestRepository;
        this.customerRepository = customerRepository;
        this.automobileRepository = automobileRepository;
        this.creditContractService = creditContractService;
        this.userService = userService;
    }

    /**
     * Cria um novo pedido de aluguel com verificação de limite de crédito
     */
    @Transactional
    public RentalRequestResponseDTO createRequest(String customerUsername, RentalRequestCreateDTO dto) {
        // Validações básicas de data
        if (dto.getReturnDate().isBefore(dto.getPickupDate())) {
            throw new IllegalArgumentException("Data de devolução deve ser posterior à data de retirada");
        }

        if (dto.getPickupDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Data de retirada deve ser no presente ou futuro");
        }

        // Buscar cliente
        Customer customer = customerRepository.findByUsername(customerUsername)
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado"));

        // Buscar automóvel
        Automobile automobile = automobileRepository.findById(dto.getAutomobileId())
                .orElseThrow(() -> new IllegalArgumentException("Automóvel não encontrado"));

        if (!automobile.isAvailable()) {
            throw new IllegalArgumentException("Automóvel não está disponível");
        }

        // Criar o pedido temporariamente para calcular o valor
        RentalRequest tempRequest = new RentalRequest();
        tempRequest.setAutomobile(automobile);
        tempRequest.setPickupDate(dto.getPickupDate());
        tempRequest.setReturnDate(dto.getReturnDate());
        tempRequest.calculateTotalValue();

        Double totalValue = tempRequest.getTotalValue();

        // NOVA LÓGICA: Verificar se o carro pertence a um agente bancário
        String carOwnerUsername = automobile.getCreatedByAgentUsername();

        if (carOwnerUsername != null) {
            // Buscar informações do dono do carro
            UserResponseDTO carOwner = userService.findByUsername(carOwnerUsername)
                    .orElse(null);

            // Se o dono é um agente bancário, verificar limite de crédito
            if (carOwner != null && carOwner.getRole() == UserRole.AGENT_BANK) {
                logger.info("Carro pertence a agente bancário {}. Verificando limite de crédito...",
                        carOwnerUsername);

                boolean hasCredit = creditContractService.hasAvailableCredit(
                        customerUsername, carOwnerUsername, totalValue);

                if (!hasCredit) {
                    logger.warn("Cliente {} não possui limite de crédito suficiente. Valor necessário: {}",
                            customerUsername, totalValue);
                    throw new IllegalArgumentException(
                            "Limite de crédito insuficiente. Entre em contato com o banco para aumentar seu limite.");
                }

                logger.info("Cliente {} possui limite de crédito suficiente", customerUsername);
            } else {
                logger.info("Carro pertence a agente empresa ou owner não encontrado. Não há verificação de crédito.");
            }
        }

        // Criar o pedido definitivo
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

        // Marcar carro como indisponível
        automobile.setAvailable(false);
        automobileRepository.save(automobile);

        RentalRequest savedRequest = rentalRequestRepository.save(request);

        logger.info("Pedido criado com sucesso: {}", savedRequest.getId());

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

    /**
     * Atualiza o status de um pedido com gerenciamento de limite de crédito
     */
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

        RequestStatus oldStatus = request.getStatus();
        RequestStatus newStatus = dto.getStatus();

        // Atualizar status do pedido
        request.changeStatus(newStatus, agentId, agentUsername);

        if (newStatus == RequestStatus.REJECTED && dto.getRejectionReason() != null) {
            request.setRejectionReason(dto.getRejectionReason());
        }

        Automobile automobile = request.getAutomobile();
        String carOwnerUsername = automobile.getCreatedByAgentUsername();

        // Verificar se o carro pertence a um agente bancário
        if (carOwnerUsername != null) {
            UserResponseDTO carOwner = userService.findByUsername(carOwnerUsername).orElse(null);

            if (carOwner != null && carOwner.getRole() == UserRole.AGENT_BANK) {
                String customerUsername = request.getCustomer().getUsername();
                Double totalValue = request.getTotalValue();

                // Gerenciar limite de crédito baseado na mudança de status
                if (newStatus == RequestStatus.APPROVED || newStatus == RequestStatus.ACTIVE) {
                    // Se estava pendente e foi aprovado, reduz o limite
                    if (oldStatus == RequestStatus.PENDING || oldStatus == RequestStatus.UNDER_ANALYSIS) {
                        logger.info("Reduzindo limite de crédito - Pedido aprovado/ativo");
                        creditContractService.reduceAvailableLimit(
                                customerUsername, carOwnerUsername, totalValue);
                    }
                } else if (newStatus == RequestStatus.REJECTED ||
                        newStatus == RequestStatus.CANCELLED ||
                        newStatus == RequestStatus.COMPLETED) {
                    // Se o pedido foi cancelado/rejeitado/concluído, restaura o limite
                    // Mas só se antes estava em um estado que tinha consumido o limite
                    if (oldStatus == RequestStatus.APPROVED ||
                            oldStatus == RequestStatus.ACTIVE ||
                            oldStatus == RequestStatus.UNDER_ANALYSIS) {
                        logger.info("Restaurando limite de crédito - Pedido {}", newStatus);
                        creditContractService.restoreAvailableLimit(
                                customerUsername, carOwnerUsername, totalValue);
                    }
                }
            }
        }

        // Gerenciar disponibilidade do automóvel
        switch (newStatus) {
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

        logger.info("Status do pedido {} atualizado: {} -> {}", id, oldStatus, newStatus);

        return convertToResponseDTO(updatedRequest);
    }

    /**
     * Cancela um pedido (cliente)
     */
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

        RequestStatus oldStatus = request.getStatus();
        request.setStatus(RequestStatus.CANCELLED);

        Automobile automobile = request.getAutomobile();
        automobile.setAvailable(true);
        automobileRepository.save(automobile);

        // Restaurar limite de crédito se aplicável
        String carOwnerUsername = automobile.getCreatedByAgentUsername();
        if (carOwnerUsername != null) {
            UserResponseDTO carOwner = userService.findByUsername(carOwnerUsername).orElse(null);

            if (carOwner != null && carOwner.getRole() == UserRole.AGENT_BANK) {
                if (oldStatus == RequestStatus.APPROVED ||
                        oldStatus == RequestStatus.ACTIVE ||
                        oldStatus == RequestStatus.UNDER_ANALYSIS) {
                    logger.info("Restaurando limite de crédito - Pedido cancelado pelo cliente");
                    creditContractService.restoreAvailableLimit(
                            customerUsername, carOwnerUsername, request.getTotalValue());
                }
            }
        }

        RentalRequest updatedRequest = rentalRequestRepository.save(request);
        return convertToResponseDTO(updatedRequest);
    }

    /**
     * Deleta um pedido (apenas pendentes)
     */
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
            customerDTO.setUsername(request.getCustomer().getUsername());
            customerDTO.setEmail(request.getCustomer().getEmail());
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