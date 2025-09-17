package com.example.backend.service;

import com.example.backend.model.Bank;
import com.example.backend.model.CreditContract;
import com.example.backend.repository.BankRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class BankService {
    private final BankRepository repo;

    public BankService(BankRepository repo) { this.repo = repo; }

    public List<Bank> findAll() { return repo.findAll(); }
    public Bank findById(String id) { return repo.findById(id).orElse(null); }
    public Bank create(Bank b) { b.setId(UUID.randomUUID().toString()); return repo.save(b); }

    public boolean grantCredit(String bankId, CreditContract contract) {
        Bank bank = repo.findById(bankId).orElse(null);
        if (bank == null) return false;
        return bank.grantCredit(contract);
    }

    public void delete(String id) { repo.deleteById(id); }
}
