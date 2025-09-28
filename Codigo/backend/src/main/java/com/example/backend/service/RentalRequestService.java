package com.example.backend.service;

import com.example.backend.model.Automobile;
import com.example.backend.model.Customer;
import com.example.backend.model.RentalRequest;
import com.example.backend.model.enums.RequestStatus;
import com.example.backend.repository.AutomobileRepository;
import com.example.backend.repository.CustomerRepository;
import com.example.backend.repository.RentalRequestRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class RentalRequestService {
    private final RentalRequestRepository repo;
    private final CustomerRepository customerRepo;
    private final AutomobileRepository autoRepo;

    public RentalRequestService(RentalRequestRepository repo,
                                CustomerRepository customerRepo,
                                AutomobileRepository autoRepo) {
        this.repo = repo;
        this.customerRepo = customerRepo;
        this.autoRepo = autoRepo;
    }

    public List<RentalRequest> findAll() { return repo.findAll(); }
    public RentalRequest findById(String id) { return repo.findById(id).orElse(null); }

    public RentalRequest create(RentalRequest r) {
        if (r.getDesiredStartDate() == null || r.getDesiredEndDate() == null)
            throw new IllegalArgumentException("Start and end dates are required");
        if (r.getDesiredEndDate().isBefore(r.getDesiredStartDate()))
            throw new IllegalArgumentException("End date must be after start date");

        if (r.getCustomer() != null && r.getCustomer().getId() != null) {
            Customer c = customerRepo.findById(r.getCustomer().getId()).orElse(null);
            r.setCustomer(c);
        }
        if (r.getAutomobile() != null && r.getAutomobile().getId() != null) {
            Automobile a = autoRepo.findById(r.getAutomobile().getId()).orElse(null);
            r.setAutomobile(a);
        }

        if (r.getAutomobile() == null) throw new IllegalArgumentException("Automobile required");
        if (!r.getAutomobile().isAvailable()) throw new IllegalArgumentException("Automobile not available");

        r.setId(UUID.randomUUID().toString());
        r.setCreationDate(LocalDate.now());
        r.setStatus(RequestStatus.CREATED);
        r.setEstimatedValue(r.calculateValue());
        return repo.save(r);
    }

    public RentalRequest changeStatus(String id, RequestStatus status) {
        return repo.findById(id).map(existing -> {
            existing.setStatus(status);
            if (status == RequestStatus.EXECUTED && existing.getAutomobile() != null) {
                Automobile a = existing.getAutomobile();
                a.setAvailable(false);
                autoRepo.save(a);
            }
            return repo.save(existing);
        }).orElse(null);
    }

    public void delete(String id) { repo.deleteById(id); }
}
