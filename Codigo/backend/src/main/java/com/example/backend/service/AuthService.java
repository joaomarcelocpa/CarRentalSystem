package com.example.backend.service;

import com.example.backend.dto.LoginRequestDTO;
import com.example.backend.dto.LoginResponseDTO;
import com.example.backend.dto.UserCreateDTO;
import com.example.backend.dto.UserResponseDTO;
import com.example.backend.exception.UserAlreadyExistsException;
import com.example.backend.exception.UserNotFoundException;
import com.example.backend.exception.InvalidUserDataException;
import com.example.backend.model.Customer;
import com.example.backend.model.CompanyAgent;
import com.example.backend.model.BankAgent;
import com.example.backend.model.Bank;
import com.example.backend.model.enums.UserRole;
import com.example.backend.repository.CustomerRepository;
import com.example.backend.repository.CompanyAgentRepository;
import com.example.backend.repository.BankAgentRepository;
import com.example.backend.repository.BankRepository;
import com.example.backend.security.JwtTokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.UUID;

@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    @Autowired
    private CustomerRepository customerRepository;
    
    @Autowired
    private CompanyAgentRepository companyAgentRepository;
    
    @Autowired
    private BankAgentRepository bankAgentRepository;
    
    @Autowired
    private BankRepository bankRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    public LoginResponseDTO login(LoginRequestDTO loginRequest) {
        // Buscar usuário por username ou email
        Object user = findUserByUsernameOrEmail(loginRequest.getUsername());

        // Verificar senha
        String storedPassword = getPassword(user);
        if (!passwordEncoder.matches(loginRequest.getPassword(), storedPassword)) {
            throw new RuntimeException("Credenciais inválidas");
        }

        // Criar autenticação manual
        String username = getUsername(user);
        UserRole role = getRole(user);

        // Gerar token JWT manualmente
        String jwt = jwtTokenProvider.generateTokenForUser(username, role);

        return new LoginResponseDTO(
            jwt,
            username,
            getEmail(user),
            role,
            86400000L // 24 horas em millisegundos
        );
    }

    public UserResponseDTO register(UserCreateDTO userCreateDTO) {
        logger.info("Starting user registration for username: {}, email: {}, role: {}",
                   userCreateDTO.getUsername(), userCreateDTO.getEmail(), userCreateDTO.getRole());

        // Validar dados básicos
        if (userCreateDTO.getUsername() == null || userCreateDTO.getUsername().trim().isEmpty()) {
            logger.warn("Username is null or empty");
            throw new InvalidUserDataException("Username é obrigatório");
        }

        if (userCreateDTO.getEmail() == null || userCreateDTO.getEmail().trim().isEmpty()) {
            logger.warn("Email is null or empty");
            throw new InvalidUserDataException("Email é obrigatório");
        }

        if (userCreateDTO.getPassword() == null || userCreateDTO.getPassword().trim().isEmpty()) {
            logger.warn("Password is null or empty");
            throw new InvalidUserDataException("Senha é obrigatória");
        }

        if (userCreateDTO.getRole() == null) {
            logger.warn("Role is null");
            throw new InvalidUserDataException("Role é obrigatório");
        }

        // Verificar se username já existe
        if (existsByUsername(userCreateDTO.getUsername())) {
            logger.warn("Username already exists: {}", userCreateDTO.getUsername());
            throw new UserAlreadyExistsException("Username já está em uso");
        }

        // Verificar se email já existe
        if (existsByEmail(userCreateDTO.getEmail())) {
            logger.warn("Email already exists: {}", userCreateDTO.getEmail());
            throw new UserAlreadyExistsException("Email já está em uso");
        }
        
        logger.info("Username and email validation passed");

        // Criar usuário
        Object user = createUserByRole(userCreateDTO);
        String userId = UUID.randomUUID().toString();
        setUserId(user, userId);
        setUsername(user, userCreateDTO.getUsername());
        setEmail(user, userCreateDTO.getEmail());
        setPassword(user, passwordEncoder.encode(userCreateDTO.getPassword()));
        setCreatedAt(user, LocalDate.now());

        logger.info("About to save user: {}", user.getClass().getSimpleName());
        
        Object savedUser = saveUser(user);
        logger.info("User saved successfully: {}", savedUser.getClass().getSimpleName());

        String savedUserId = getUserId(savedUser);
        logger.info("Got user ID: {}", savedUserId);
        
        String username = getUsername(savedUser);
        logger.info("Got username: {}", username);
        
        String email = getEmail(savedUser);
        logger.info("Got email: {}", email);
        
        UserRole role = getRole(savedUser);
        logger.info("Got role: {}", role);
        
        LocalDate createdAt = getCreatedAt(savedUser);
        logger.info("Got createdAt: {}", createdAt);

        logger.info("Creating UserResponseDTO - ID: {}, Username: {}, Email: {}, Role: {}, CreatedAt: {}", 
                   savedUserId, username, email, role, createdAt);

        UserResponseDTO response = new UserResponseDTO(savedUserId, username, email, role, createdAt);
        logger.info("UserResponseDTO created successfully");
        
        return response;
    }

    public UserResponseDTO getCurrentUser(String username) {
        Object user = findUserByUsername(username);

        return new UserResponseDTO(
            getUserId(user),
            getUsername(user),
            getEmail(user),
            getRole(user),
            getCreatedAt(user)
        );
    }

    private Object findUserByUsername(String username) {
        // Tentar encontrar em cada repositório
        var customer = customerRepository.findByUsername(username);
        if (customer.isPresent()) return customer.get();

        var companyAgent = companyAgentRepository.findByUsername(username);
        if (companyAgent.isPresent()) return companyAgent.get();

        var bankAgent = bankAgentRepository.findByUsername(username);
        if (bankAgent.isPresent()) return bankAgent.get();

        var bank = bankRepository.findByUsername(username);
        if (bank.isPresent()) return bank.get();

        throw new UserNotFoundException("Usuário não encontrado");
    }

    private Object findUserByUsernameOrEmail(String identifier) {
        logger.info("Searching for user with identifier: {}", identifier);
        // Primeiro, tentar buscar por username
        try {
            logger.info("Trying to find user by username: {}", identifier);
            Object user = findUserByUsername(identifier);
            logger.info("Found user by username: {}", user.getClass().getSimpleName());
            return user;
        } catch (UserNotFoundException e) {
            logger.info("User not found by username, trying by email: {}", identifier);
            // Se não encontrou por username, tentar por email
            Object user = findUserByEmail(identifier);
            logger.info("Found user by email: {}", user.getClass().getSimpleName());
            return user;
        }
    }

    private Object findUserByEmail(String email) {
        logger.info("Searching for user by email: {}", email);
        // Tentar encontrar por email em cada repositório
        var customer = customerRepository.findByEmail(email);
        logger.info("Customer search result: {}", customer.isPresent() ? "found" : "not found");
        if (customer.isPresent()) return customer.get();

        var companyAgent = companyAgentRepository.findByEmail(email);
        logger.info("CompanyAgent search result: {}", companyAgent.isPresent() ? "found" : "not found");
        if (companyAgent.isPresent()) return companyAgent.get();

        var bankAgent = bankAgentRepository.findByEmail(email);
        logger.info("BankAgent search result: {}", bankAgent.isPresent() ? "found" : "not found");
        if (bankAgent.isPresent()) return bankAgent.get();

        var bank = bankRepository.findByEmail(email);
        logger.info("Bank search result: {}", bank.isPresent() ? "found" : "not found");
        if (bank.isPresent()) return bank.get();

        logger.warn("No user found with email: {}", email);
        throw new UserNotFoundException("Usuário não encontrado com email: " + email);
    }

    private Object createUserByRole(UserCreateDTO dto) {
        switch (dto.getRole()) {
            case CUSTOMER:
                Customer customer = new Customer();
                customer.setRole(UserRole.CUSTOMER);
                return customer;
            case AGENT_COMPANY:
                CompanyAgent companyAgent = new CompanyAgent();
                companyAgent.setRole(UserRole.AGENT_COMPANY);
                return companyAgent;
            case AGENT_BANK:
                BankAgent bankAgent = new BankAgent();
                bankAgent.setRole(UserRole.AGENT_BANK);
                return bankAgent;
            default:
                logger.error("Unsupported role: {}", dto.getRole());
                throw new InvalidUserDataException("Role não suportado: " + dto.getRole());
        }
    }

    private Object saveUser(Object user) {
        logger.info("saveUser called with type: {}", user.getClass().getSimpleName());
        
        try {
            if (user instanceof Customer customer) {
                logger.info("Saving Customer with ID: {}, Username: {}, Email: {}, Role: {}", 
                           customer.getId(), customer.getUsername(), customer.getEmail(), customer.getRole());
                Customer saved = customerRepository.save(customer);
                logger.info("Customer saved successfully with ID: {}", saved.getId());
                return saved;
            } else if (user instanceof CompanyAgent agent) {
                logger.info("Saving CompanyAgent");
                return companyAgentRepository.save(agent);
            } else if (user instanceof BankAgent agent) {
                logger.info("Saving BankAgent");
                return bankAgentRepository.save(agent);
            } else if (user instanceof Bank bank) {
                logger.info("Saving Bank");
                return bankRepository.save(bank);
            }
            logger.error("Unsupported user type: {}", user.getClass().getName());
            throw new InvalidUserDataException("Tipo de usuário não suportado");
        } catch (Exception e) {
            logger.error("Error saving user: {}", e.getMessage(), e);
            throw e;
        }
    }

    private boolean existsByUsername(String username) {
        return customerRepository.existsByUsername(username) ||
               companyAgentRepository.existsByUsername(username) ||
               bankAgentRepository.existsByUsername(username) ||
               bankRepository.existsByUsername(username);
    }

    private boolean existsByEmail(String email) {
        return customerRepository.existsByEmail(email) ||
               companyAgentRepository.existsByEmail(email) ||
               bankAgentRepository.existsByEmail(email) ||
               bankRepository.existsByEmail(email);
    }

    // Métodos auxiliares para acessar propriedades
    private String getUsername(Object user) {
        if (user instanceof Customer customer) {
            logger.debug("Getting username from Customer: {}", customer.getUsername());
            return customer.getUsername();
        }
        if (user instanceof CompanyAgent agent) {
            logger.debug("Getting username from CompanyAgent: {}", agent.getUsername());
            return agent.getUsername();
        }
        if (user instanceof BankAgent agent) {
            logger.debug("Getting username from BankAgent: {}", agent.getUsername());
            return agent.getUsername();
        }
        if (user instanceof Bank bank) {
            logger.debug("Getting username from Bank: {}", bank.getUsername());
            return bank.getUsername();
        }
        logger.warn("Unknown user type for username: {}", user.getClass().getName());
        return null;
    }

    private String getEmail(Object user) {
        if (user instanceof Customer customer) return customer.getEmail();
        if (user instanceof CompanyAgent agent) return agent.getEmail();
        if (user instanceof BankAgent agent) return agent.getEmail();
        if (user instanceof Bank bank) return bank.getEmail();
        return null;
    }

    private String getPassword(Object user) {
        if (user instanceof Customer customer) return customer.getPassword();
        if (user instanceof CompanyAgent agent) return agent.getPassword();
        if (user instanceof BankAgent agent) return agent.getPassword();
        if (user instanceof Bank bank) return bank.getPassword();
        return null;
    }

    private UserRole getRole(Object user) {
        if (user instanceof Customer customer) return customer.getRole();
        if (user instanceof CompanyAgent agent) return agent.getRole();
        if (user instanceof BankAgent agent) return agent.getRole();
        if (user instanceof Bank bank) return bank.getRole();
        return null;
    }

    private String getUserId(Object user) {
        if (user instanceof Customer customer) {
            logger.debug("Getting ID from Customer: {}", customer.getId());
            return customer.getId();
        }
        if (user instanceof CompanyAgent agent) {
            logger.debug("Getting ID from CompanyAgent: {}", agent.getId());
            return agent.getId();
        }
        if (user instanceof BankAgent agent) {
            logger.debug("Getting ID from BankAgent: {}", agent.getId());
            return agent.getId();
        }
        if (user instanceof Bank bank) {
            logger.debug("Getting ID from Bank: {}", bank.getId());
            return bank.getId();
        }
        logger.warn("Unknown user type: {}", user.getClass().getName());
        return null;
    }

    private LocalDate getCreatedAt(Object user) {
        if (user instanceof Customer customer) return customer.getCreatedAt();
        if (user instanceof CompanyAgent agent) return agent.getCreatedAt();
        if (user instanceof BankAgent agent) return agent.getCreatedAt();
        if (user instanceof Bank bank) return bank.getCreatedAt();
        return null;
    }

    private void setUserId(Object user, String id) {
        if (user instanceof Customer customer) customer.setId(id);
        else if (user instanceof CompanyAgent agent) agent.setId(id);
        else if (user instanceof BankAgent agent) agent.setId(id);
        else if (user instanceof Bank bank) bank.setId(id);
    }

    private void setPassword(Object user, String password) {
        if (user instanceof Customer customer) customer.setPassword(password);
        else if (user instanceof CompanyAgent agent) agent.setPassword(password);
        else if (user instanceof BankAgent agent) agent.setPassword(password);
        else if (user instanceof Bank bank) bank.setPassword(password);
    }

    private void setCreatedAt(Object user, LocalDate date) {
        if (user instanceof Customer customer) customer.setCreatedAt(date);
        else if (user instanceof CompanyAgent agent) agent.setCreatedAt(date);
        else if (user instanceof BankAgent agent) agent.setCreatedAt(date);
        else if (user instanceof Bank bank) bank.setCreatedAt(date);
    }

    private void setUsername(Object user, String username) {
        if (user instanceof Customer customer) customer.setUsername(username);
        else if (user instanceof CompanyAgent agent) agent.setUsername(username);
        else if (user instanceof BankAgent agent) agent.setUsername(username);
        else if (user instanceof Bank bank) bank.setUsername(username);
    }

    private void setEmail(Object user, String email) {
        if (user instanceof Customer customer) customer.setEmail(email);
        else if (user instanceof CompanyAgent agent) agent.setEmail(email);
        else if (user instanceof BankAgent agent) agent.setEmail(email);
        else if (user instanceof Bank bank) bank.setEmail(email);
    }
}