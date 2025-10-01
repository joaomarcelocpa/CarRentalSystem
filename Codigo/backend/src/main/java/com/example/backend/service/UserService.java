package com.example.backend.service;

import com.example.backend.dto.UserCreateDTO;
import com.example.backend.dto.UserResponseDTO;
import com.example.backend.model.Customer;
import com.example.backend.model.CompanyAgent;
import com.example.backend.model.BankAgent;
import com.example.backend.model.Bank;
import com.example.backend.model.enums.UserRole;
import com.example.backend.repository.CustomerRepository;
import com.example.backend.repository.CompanyAgentRepository;
import com.example.backend.repository.BankAgentRepository;
import com.example.backend.repository.BankRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CompanyAgentRepository companyAgentRepository;

    @Autowired
    private BankAgentRepository bankAgentRepository;

    @Autowired
    private BankRepository bankRepository;

    public List<UserResponseDTO> findAll() {
        List<UserResponseDTO> customers = customerRepository.findAll().stream()
                .map(this::convertCustomerToResponseDTO)
                .collect(Collectors.toList());

        List<UserResponseDTO> companyAgents = companyAgentRepository.findAll().stream()
                .map(this::convertCompanyAgentToResponseDTO)
                .collect(Collectors.toList());

        List<UserResponseDTO> bankAgents = bankAgentRepository.findAll().stream()
                .map(this::convertBankAgentToResponseDTO)
                .collect(Collectors.toList());

        List<UserResponseDTO> banks = bankRepository.findAll().stream()
                .map(this::convertBankToResponseDTO)
                .collect(Collectors.toList());

        customers.addAll(companyAgents);
        customers.addAll(bankAgents);
        customers.addAll(banks);

        return customers;
    }

    public Optional<UserResponseDTO> findById(String id) {
        // Tentar encontrar em cada repositório
        Optional<Customer> customer = customerRepository.findById(id);
        if (customer.isPresent()) {
            return Optional.of(convertCustomerToResponseDTO(customer.get()));
        }

        Optional<CompanyAgent> companyAgent = companyAgentRepository.findById(id);
        if (companyAgent.isPresent()) {
            return Optional.of(convertCompanyAgentToResponseDTO(companyAgent.get()));
        }

        Optional<BankAgent> bankAgent = bankAgentRepository.findById(id);
        if (bankAgent.isPresent()) {
            return Optional.of(convertBankAgentToResponseDTO(bankAgent.get()));
        }

        Optional<Bank> bank = bankRepository.findById(id);
        if (bank.isPresent()) {
            return Optional.of(convertBankToResponseDTO(bank.get()));
        }

        return Optional.empty();
    }

    public Optional<UserResponseDTO> findByEmail(String email) {
        // Tentar encontrar em cada repositório
        Optional<Customer> customer = customerRepository.findByEmail(email);
        if (customer.isPresent()) {
            return Optional.of(convertCustomerToResponseDTO(customer.get()));
        }

        Optional<CompanyAgent> companyAgent = companyAgentRepository.findByEmail(email);
        if (companyAgent.isPresent()) {
            return Optional.of(convertCompanyAgentToResponseDTO(companyAgent.get()));
        }

        Optional<BankAgent> bankAgent = bankAgentRepository.findByEmail(email);
        if (bankAgent.isPresent()) {
            return Optional.of(convertBankAgentToResponseDTO(bankAgent.get()));
        }

        Optional<Bank> bank = bankRepository.findByEmail(email);
        if (bank.isPresent()) {
            return Optional.of(convertBankToResponseDTO(bank.get()));
        }

        return Optional.empty();
    }

    public List<UserResponseDTO> findByRole(UserRole role) {
        switch (role) {
            case CUSTOMER:
                return customerRepository.findAll().stream()
                        .map(this::convertCustomerToResponseDTO)
                        .collect(Collectors.toList());
            case AGENT_COMPANY:
                return companyAgentRepository.findAll().stream()
                        .map(this::convertCompanyAgentToResponseDTO)
                        .collect(Collectors.toList());
            case AGENT_BANK:
                List<UserResponseDTO> bankAgents = bankAgentRepository.findAll().stream()
                        .map(this::convertBankAgentToResponseDTO)
                        .collect(Collectors.toList());
                List<UserResponseDTO> banks = bankRepository.findAll().stream()
                        .map(this::convertBankToResponseDTO)
                        .collect(Collectors.toList());
                bankAgents.addAll(banks);
                return bankAgents;
            default:
                return List.of();
        }
    }

    public UserResponseDTO create(UserCreateDTO dto) {
        switch (dto.getRole()) {
            case CUSTOMER:
                Customer customer = new Customer();
                customer.setId(UUID.randomUUID().toString());
                customer.setUsername(dto.getUsername());
                customer.setEmail(dto.getEmail());
                customer.setPassword(dto.getPassword());
                customer.setCreatedAt(LocalDate.now());
                Customer savedCustomer = customerRepository.save(customer);
                return convertCustomerToResponseDTO(savedCustomer);

            case AGENT_COMPANY:
                CompanyAgent companyAgent = new CompanyAgent();
                companyAgent.setId(UUID.randomUUID().toString());
                companyAgent.setUsername(dto.getUsername());
                companyAgent.setEmail(dto.getEmail());
                companyAgent.setPassword(dto.getPassword());
                companyAgent.setCreatedAt(LocalDate.now());
                CompanyAgent savedCompanyAgent = companyAgentRepository.save(companyAgent);
                return convertCompanyAgentToResponseDTO(savedCompanyAgent);

            case AGENT_BANK:
                BankAgent bankAgent = new BankAgent();
                bankAgent.setId(UUID.randomUUID().toString());
                bankAgent.setUsername(dto.getUsername());
                bankAgent.setEmail(dto.getEmail());
                bankAgent.setPassword(dto.getPassword());
                bankAgent.setCreatedAt(LocalDate.now());
                BankAgent savedBankAgent = bankAgentRepository.save(bankAgent);
                return convertBankAgentToResponseDTO(savedBankAgent);

            default:
                throw new IllegalArgumentException("Role não suportado: " + dto.getRole());
        }
    }

    public Optional<UserResponseDTO> update(String id, UserCreateDTO dto) {
        // Tentar encontrar em cada repositório
        Optional<Customer> customer = customerRepository.findById(id);
        if (customer.isPresent()) {
            Customer c = customer.get();
            c.setUsername(dto.getUsername());
            c.setEmail(dto.getEmail());
            c.setPassword(dto.getPassword());
            c.setRole(dto.getRole());
            Customer updated = customerRepository.save(c);
            return Optional.of(convertCustomerToResponseDTO(updated));
        }

        Optional<CompanyAgent> companyAgent = companyAgentRepository.findById(id);
        if (companyAgent.isPresent()) {
            CompanyAgent agent = companyAgent.get();
            agent.setUsername(dto.getUsername());
            agent.setEmail(dto.getEmail());
            agent.setPassword(dto.getPassword());
            agent.setRole(dto.getRole());
            CompanyAgent updated = companyAgentRepository.save(agent);
            return Optional.of(convertCompanyAgentToResponseDTO(updated));
        }

        Optional<BankAgent> bankAgent = bankAgentRepository.findById(id);
        if (bankAgent.isPresent()) {
            BankAgent agent = bankAgent.get();
            agent.setUsername(dto.getUsername());
            agent.setEmail(dto.getEmail());
            agent.setPassword(dto.getPassword());
            agent.setRole(dto.getRole());
            BankAgent updated = bankAgentRepository.save(agent);
            return Optional.of(convertBankAgentToResponseDTO(updated));
        }

        Optional<Bank> bank = bankRepository.findById(id);
        if (bank.isPresent()) {
            Bank b = bank.get();
            b.setUsername(dto.getUsername());
            b.setEmail(dto.getEmail());
            b.setPassword(dto.getPassword());
            b.setRole(dto.getRole());
            Bank updated = bankRepository.save(b);
            return Optional.of(convertBankToResponseDTO(updated));
        }

        return Optional.empty();
    }

    public boolean delete(String id) {
        if (customerRepository.existsById(id)) {
            customerRepository.deleteById(id);
            return true;
        }
        if (companyAgentRepository.existsById(id)) {
            companyAgentRepository.deleteById(id);
            return true;
        }
        if (bankAgentRepository.existsById(id)) {
            bankAgentRepository.deleteById(id);
            return true;
        }
        if (bankRepository.existsById(id)) {
            bankRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public boolean existsByEmail(String email) {
        return customerRepository.existsByEmail(email) ||
               companyAgentRepository.existsByEmail(email) ||
               bankAgentRepository.existsByEmail(email) ||
               bankRepository.existsByEmail(email);
    }

    public boolean existsByUsername(String username) {
        return customerRepository.existsByUsername(username) ||
               companyAgentRepository.existsByUsername(username) ||
               bankAgentRepository.existsByUsername(username) ||
               bankRepository.existsByUsername(username);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Tentar encontrar em cada repositório
        Optional<Customer> customer = customerRepository.findByUsername(username);
        if (customer.isPresent()) {
            return createUserDetails(customer.get());
        }

        Optional<CompanyAgent> companyAgent = companyAgentRepository.findByUsername(username);
        if (companyAgent.isPresent()) {
            return createUserDetails(companyAgent.get());
        }

        Optional<BankAgent> bankAgent = bankAgentRepository.findByUsername(username);
        if (bankAgent.isPresent()) {
            return createUserDetails(bankAgent.get());
        }

        Optional<Bank> bank = bankRepository.findByUsername(username);
        if (bank.isPresent()) {
            return createUserDetails(bank.get());
        }

        throw new UsernameNotFoundException("Usuário não encontrado: " + username);
    }

    private UserDetails createUserDetails(Object user) {
        if (user instanceof Customer customer) {
            return new org.springframework.security.core.userdetails.User(
                    customer.getUsername(),
                    customer.getPassword(),
                    true, true, true, true,
                    getAuthorities(customer.getRole())
            );
        } else if (user instanceof CompanyAgent agent) {
            return new org.springframework.security.core.userdetails.User(
                    agent.getUsername(),
                    agent.getPassword(),
                    true, true, true, true,
                    getAuthorities(agent.getRole())
            );
        } else if (user instanceof BankAgent agent) {
            return new org.springframework.security.core.userdetails.User(
                    agent.getUsername(),
                    agent.getPassword(),
                    true, true, true, true,
                    getAuthorities(agent.getRole())
            );
        } else if (user instanceof Bank bank) {
            return new org.springframework.security.core.userdetails.User(
                    bank.getUsername(),
                    bank.getPassword(),
                    true, true, true, true,
                    getAuthorities(bank.getRole())
            );
        }
        throw new UsernameNotFoundException("Tipo de usuário não suportado");
    }

    private java.util.Collection<? extends org.springframework.security.core.GrantedAuthority> getAuthorities(UserRole role) {
        java.util.List<org.springframework.security.core.GrantedAuthority> authorities = new java.util.ArrayList<>();

        authorities.add(new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_" + role.name()));

        switch (role) {
            case CUSTOMER:
                authorities.add(new org.springframework.security.core.authority.SimpleGrantedAuthority("PERM_CUSTOMER_READ"));
                authorities.add(new org.springframework.security.core.authority.SimpleGrantedAuthority("PERM_CUSTOMER_WRITE"));
                authorities.add(new org.springframework.security.core.authority.SimpleGrantedAuthority("PERM_RENTAL_REQUEST_CREATE"));
                authorities.add(new org.springframework.security.core.authority.SimpleGrantedAuthority("PERM_RENTAL_REQUEST_READ_OWN"));
                break;
            case AGENT_COMPANY:
                authorities.add(new org.springframework.security.core.authority.SimpleGrantedAuthority("PERM_AGENT_READ"));
                authorities.add(new org.springframework.security.core.authority.SimpleGrantedAuthority("PERM_AGENT_WRITE"));
                authorities.add(new org.springframework.security.core.authority.SimpleGrantedAuthority("PERM_RENTAL_REQUEST_READ_ALL"));
                authorities.add(new org.springframework.security.core.authority.SimpleGrantedAuthority("PERM_RENTAL_REQUEST_UPDATE"));
                authorities.add(new org.springframework.security.core.authority.SimpleGrantedAuthority("PERM_AUTOMOBILE_MANAGE"));
                break;
            case AGENT_BANK:
                authorities.add(new org.springframework.security.core.authority.SimpleGrantedAuthority("PERM_AGENT_READ"));
                authorities.add(new org.springframework.security.core.authority.SimpleGrantedAuthority("PERM_AGENT_WRITE"));
                authorities.add(new org.springframework.security.core.authority.SimpleGrantedAuthority("PERM_RENTAL_REQUEST_READ_ALL"));
                authorities.add(new org.springframework.security.core.authority.SimpleGrantedAuthority("PERM_RENTAL_REQUEST_UPDATE"));
                authorities.add(new org.springframework.security.core.authority.SimpleGrantedAuthority("PERM_CREDIT_CONTRACT_MANAGE"));
                authorities.add(new org.springframework.security.core.authority.SimpleGrantedAuthority("PERM_FINANCIAL_ANALYSIS"));
                authorities.add(new org.springframework.security.core.authority.SimpleGrantedAuthority("PERM_AUTOMOBILE_MANAGE")); // ADICIONADO
                break;
        }

        return authorities;
    }

    private UserResponseDTO convertCustomerToResponseDTO(Customer customer) {
        return new UserResponseDTO(
                customer.getId(),
                customer.getUsername(),
                customer.getEmail(),
                customer.getRole(),
                customer.getCreatedAt()
        );
    }

    private UserResponseDTO convertCompanyAgentToResponseDTO(CompanyAgent agent) {
        return new UserResponseDTO(
                agent.getId(),
                agent.getUsername(),
                agent.getEmail(),
                agent.getRole(),
                agent.getCreatedAt()
        );
    }

    private UserResponseDTO convertBankAgentToResponseDTO(BankAgent agent) {
        return new UserResponseDTO(
                agent.getId(),
                agent.getUsername(),
                agent.getEmail(),
                agent.getRole(),
                agent.getCreatedAt()
        );
    }

    private UserResponseDTO convertBankToResponseDTO(Bank bank) {
        return new UserResponseDTO(
                bank.getId(),
                bank.getUsername(),
                bank.getEmail(),
                bank.getRole(),
                bank.getCreatedAt()
        );
    }

    public Optional<UserResponseDTO> findByUsername(String username) {
        Optional<Customer> customer = customerRepository.findByUsername(username);
        if (customer.isPresent()) {
            return Optional.of(convertCustomerToResponseDTO(customer.get()));
        }

        Optional<CompanyAgent> companyAgent = companyAgentRepository.findByUsername(username);
        if (companyAgent.isPresent()) {
            return Optional.of(convertCompanyAgentToResponseDTO(companyAgent.get()));
        }

        Optional<BankAgent> bankAgent = bankAgentRepository.findByUsername(username);
        if (bankAgent.isPresent()) {
            return Optional.of(convertBankAgentToResponseDTO(bankAgent.get()));
        }

        Optional<Bank> bank = bankRepository.findByUsername(username);
        if (bank.isPresent()) {
            return Optional.of(convertBankToResponseDTO(bank.get()));
        }

        return Optional.empty();
    }
}