package com.example.backend.service;

import com.example.backend.dto.*;
import com.example.backend.model.Customer;
import com.example.backend.repository.CustomerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CustomerService {
    private final CustomerRepository repo;
    public CustomerService(CustomerRepository repo) { this.repo = repo; }

    public List<Customer> findAll() { return repo.findAll(); }
    public Customer findById(String id) { return repo.findById(id).orElse(null); }
    public Customer create(Customer c) {
        c.setId(UUID.randomUUID().toString());
        return repo.save(c);
    }
    public Customer update(String id, Customer updated) {
        return repo.findById(id).map(existing -> {
            existing.setName(updated.getName());
            existing.setEmailContact(updated.getEmailContact());
            existing.setAddress(updated.getAddress());
            existing.setProfession(updated.getProfession());
            return repo.save(existing);
        }).orElse(null);
    }
    public void delete(String id) { repo.deleteById(id); }

    public List<CustomerResponseDTO> findAllAsDTO() {
        return repo.findAll().stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    public CustomerResponseDTO findByIdAsDTO(String id) {
        Customer customer = repo.findById(id).orElse(null);
        return customer != null ? toResponseDTO(customer) : null;
    }

    public CustomerResponseDTO createFromDTO(CustomerCreateDTO dto) {
        Customer customer = fromCreateDTO(dto);
        customer.setId(UUID.randomUUID().toString());
        Customer saved = repo.save(customer);
        return toResponseDTO(saved);
    }

    public CustomerResponseDTO updateFromDTO(String id, CustomerCreateDTO dto) {
        return repo.findById(id).map(existing -> {
            existing.setName(dto.getName());
            existing.setEmailContact(dto.getEmailContact());
            existing.setRg(dto.getRg());
            existing.setCpf(dto.getCpf());
            existing.setAddress(dto.getAddress());
            existing.setProfession(dto.getProfession());
            Customer saved = repo.save(existing);
            return toResponseDTO(saved);
        }).orElse(null);
    }

    @Transactional
    public CustomerResponseDTO updateCreditLimit(String customerId, Double credit_limit) {
        return repo.findById(customerId).map(customer -> {
            customer.setCreditLimit(credit_limit);
            Customer saved = repo.save(customer);
            return toResponseDTO(saved);
        }).orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado"));
    }

    private CustomerResponseDTO toResponseDTO(Customer customer) {
        CustomerResponseDTO dto = new CustomerResponseDTO();
        dto.setId(customer.getId());
        dto.setName(customer.getName());
        dto.setEmailContact(customer.getEmailContact());
        dto.setRg(customer.getRg());
        dto.setCpf(customer.getCpf());
        dto.setAddress(customer.getAddress());
        dto.setProfession(customer.getProfession());
        dto.setCreatedAt(customer.getCreatedAt());
        dto.setCreditLimit(customer.getCreditLimit());

        if (customer.getRentalRequests() != null) {
            List<RentalRequestSummaryDTO> requests = customer.getRentalRequests().stream()
                    .map(this::toRentalRequestSummaryDTO)
                    .collect(Collectors.toList());
            dto.setRentalRequests(requests);
        }

        return dto;
    }

    private Customer fromCreateDTO(CustomerCreateDTO dto) {
        Customer customer = new Customer();
        customer.setName(dto.getName());
        customer.setEmailContact(dto.getEmailContact());
        customer.setRg(dto.getRg());
        customer.setCpf(dto.getCpf());
        customer.setAddress(dto.getAddress());
        customer.setProfession(dto.getProfession());
        return customer;
    }

    private RentalRequestSummaryDTO toRentalRequestSummaryDTO(com.example.backend.model.RentalRequest request) {
        RentalRequestSummaryDTO dto = new RentalRequestSummaryDTO();
        dto.setId(request.getId());
        dto.setDesiredStartDate(request.getPickupDate());
        dto.setDesiredEndDate(request.getReturnDate());
        dto.setStatus(request.getStatus() != null ? request.getStatus().toString() : null);
        dto.setEstimatedValue(request.getTotalValue());
        return dto;
    }
}