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
        logger.info("Login attempt for username: {}", loginRequest.getUsername());

        Object user = findUserByUsernameOrEmail(loginRequest.getUsername());

        String storedPassword = getPassword(user);
        if (!passwordEncoder.matches(loginRequest.getPassword(), storedPassword)) {
            logger.warn("Invalid password for user: {}", loginRequest.getUsername());
            throw new RuntimeException("Credenciais inválidas");
        }

        String username = getUsername(user);
        String userId = getUserId(user);
        UserRole role = getRole(user);

        logger.info("Login successful - Username: {}, UserId: {}, Role: {}", username, userId, role);

        // CORREÇÃO: Garantir que o userId seja salvo no token
        String jwt = jwtTokenProvider.generateTokenForUser(username, userId, role);

        return new LoginResponseDTO(
                jwt,
                username,
                getEmail(user),
                role,
                86400000L
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

        logger.info("About to save user - ID: {}, Username: {}, Role: {}",
                userId, userCreateDTO.getUsername(), userCreateDTO.getRole());

        Object savedUser = saveUser(user);
        logger.info("User saved successfully: {}", savedUser.getClass().getSimpleName());

        String savedUserId = getUserId(savedUser);
        String username = getUsername(savedUser);
        String email = getEmail(savedUser);
        UserRole role = getRole(savedUser);
        LocalDate createdAt = getCreatedAt(savedUser);

        logger.info("User created - ID: {}, Username: {}, Email: {}, Role: {}",
                savedUserId, username, email, role);

        return new UserResponseDTO(savedUserId, username, email, role, createdAt);
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
        logger.debug("Searching for user by username: {}", username);

        // Tentar encontrar em cada repositório
        var customer = customerRepository.findByUsername(username);
        if (customer.isPresent()) {
            logger.debug("Found Customer with ID: {}", customer.get().getId());
            return customer.get();
        }

        var companyAgent = companyAgentRepository.findByUsername(username);
        if (companyAgent.isPresent()) {
            logger.debug("Found CompanyAgent with ID: {}", companyAgent.get().getId());
            return companyAgent.get();
        }

        var bankAgent = bankAgentRepository.findByUsername(username);
        if (bankAgent.isPresent()) {
            logger.debug("Found BankAgent with ID: {}", bankAgent.get().getId());
            return bankAgent.get();
        }

        var bank = bankRepository.findByUsername(username);
        if (bank.isPresent()) {
            logger.debug("Found Bank with ID: {}", bank.get().getId());
            return bank.get();
        }

        logger.error("User not found: {}", username);
        throw new UserNotFoundException("Usuário não encontrado");
    }

    private Object findUserByUsernameOrEmail(String identifier) {
        logger.info("Searching for user with identifier: {}", identifier);

        try {
            Object user = findUserByUsername(identifier);
            logger.info("Found user by username: {}, ID: {}",
                    getUsername(user), getUserId(user));
            return user;
        } catch (UserNotFoundException e) {
            logger.info("User not found by username, trying by email: {}", identifier);
            Object user = findUserByEmail(identifier);
            logger.info("Found user by email: {}, ID: {}",
                    getUsername(user), getUserId(user));
            return user;
        }
    }

    private Object findUserByEmail(String email) {
        logger.debug("Searching for user by email: {}", email);

        var customer = customerRepository.findByEmail(email);
        if (customer.isPresent()) {
            logger.debug("Found Customer by email, ID: {}", customer.get().getId());
            return customer.get();
        }

        var companyAgent = companyAgentRepository.findByEmail(email);
        if (companyAgent.isPresent()) {
            logger.debug("Found CompanyAgent by email, ID: {}", companyAgent.get().getId());
            return companyAgent.get();
        }

        var bankAgent = bankAgentRepository.findByEmail(email);
        if (bankAgent.isPresent()) {
            logger.debug("Found BankAgent by email, ID: {}", bankAgent.get().getId());
            return bankAgent.get();
        }

        var bank = bankRepository.findByEmail(email);
        if (bank.isPresent()) {
            logger.debug("Found Bank by email, ID: {}", bank.get().getId());
            return bank.get();
        }

        logger.error("No user found with email: {}", email);
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
                logger.info("Saving Customer with ID: {}, Username: {}",
                        customer.getId(), customer.getUsername());
                Customer saved = customerRepository.save(customer);
                logger.info("Customer saved successfully with ID: {}", saved.getId());
                return saved;
            } else if (user instanceof CompanyAgent agent) {
                logger.info("Saving CompanyAgent with ID: {}, Username: {}",
                        agent.getId(), agent.getUsername());
                CompanyAgent saved = companyAgentRepository.save(agent);
                logger.info("CompanyAgent saved successfully with ID: {}", saved.getId());
                return saved;
            } else if (user instanceof BankAgent agent) {
                logger.info("Saving BankAgent with ID: {}, Username: {}",
                        agent.getId(), agent.getUsername());
                BankAgent saved = bankAgentRepository.save(agent);
                logger.info("BankAgent saved successfully with ID: {}", saved.getId());
                return saved;
            } else if (user instanceof Bank bank) {
                logger.info("Saving Bank with ID: {}, Username: {}",
                        bank.getId(), bank.getUsername());
                Bank saved = bankRepository.save(bank);
                logger.info("Bank saved successfully with ID: {}", saved.getId());
                return saved;
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
        if (user instanceof Customer customer) return customer.getUsername();
        if (user instanceof CompanyAgent agent) return agent.getUsername();
        if (user instanceof BankAgent agent) return agent.getUsername();
        if (user instanceof Bank bank) return bank.getUsername();
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
        String id = null;
        if (user instanceof Customer customer) {
            id = customer.getId();
        } else if (user instanceof CompanyAgent agent) {
            id = agent.getId();
        } else if (user instanceof BankAgent agent) {
            id = agent.getId();
        } else if (user instanceof Bank bank) {
            id = bank.getId();
        }

        logger.debug("getUserId - Type: {}, ID: {}",
                user.getClass().getSimpleName(), id);
        return id;
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
        logger.debug("setUserId - Type: {}, ID set to: {}",
                user.getClass().getSimpleName(), id);
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