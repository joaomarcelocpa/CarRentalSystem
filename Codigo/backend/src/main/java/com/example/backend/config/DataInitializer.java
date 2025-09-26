package com.example.backend.config;

import com.example.backend.model.Automobile;
import com.example.backend.model.Customer;
import com.example.backend.model.CompanyAgent;
import com.example.backend.model.BankAgent;
import com.example.backend.model.Bank;
import com.example.backend.repository.AutomobileRepository;
import com.example.backend.repository.CustomerRepository;
import com.example.backend.repository.CompanyAgentRepository;
import com.example.backend.repository.BankAgentRepository;
import com.example.backend.repository.BankRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.UUID;

@Component
public class DataInitializer implements CommandLineRunner {

    private final AutomobileRepository automobileRepository;
    private final CustomerRepository customerRepository;
    private final CompanyAgentRepository companyAgentRepository;
    private final BankAgentRepository bankAgentRepository;
    private final BankRepository bankRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(AutomobileRepository automobileRepository,
                           CustomerRepository customerRepository,
                           CompanyAgentRepository companyAgentRepository,
                           BankAgentRepository bankAgentRepository,
                           BankRepository bankRepository,
                           PasswordEncoder passwordEncoder) {
        this.automobileRepository = automobileRepository;
        this.customerRepository = customerRepository;
        this.companyAgentRepository = companyAgentRepository;
        this.bankAgentRepository = bankAgentRepository;
        this.bankRepository = bankRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        initializeAutomobiles();
        initializeCustomers();
        initializeUsers();
    }

    private void initializeAutomobiles() {
        if (automobileRepository.count() > 0) {
            return;
        }

        Automobile car1 = new Automobile();
        car1.setId(UUID.randomUUID().toString());
        car1.setBrand("Toyota");
        car1.setModel("Corolla");
        car1.setYear(2023);
        car1.setDailyRate(120.0);
        car1.setAvailable(true);
        car1.setRegistration("ABC-2023");
        car1.setLicensePlate("ABC-1234");
        automobileRepository.save(car1);

        Automobile car2 = new Automobile();
        car2.setId(UUID.randomUUID().toString());
        car2.setBrand("Honda");
        car2.setModel("Civic");
        car2.setYear(2023);
        car2.setDailyRate(130.0);
        car2.setAvailable(true);
        car2.setRegistration("DEF-2023");
        car2.setLicensePlate("DEF-5678");
        automobileRepository.save(car2);

        Automobile car3 = new Automobile();
        car3.setId(UUID.randomUUID().toString());
        car3.setBrand("Volkswagen");
        car3.setModel("Jetta");
        car3.setYear(2022);
        car3.setDailyRate(110.0);
        car3.setAvailable(true);
        car3.setRegistration("GHI-2022");
        car3.setLicensePlate("GHI-9012");
        automobileRepository.save(car3);

        Automobile car4 = new Automobile();
        car4.setId(UUID.randomUUID().toString());
        car4.setBrand("Hyundai");
        car4.setModel("Elantra");
        car4.setYear(2023);
        car4.setDailyRate(115.0);
        car4.setAvailable(true);
        car4.setRegistration("JKL-2023");
        car4.setLicensePlate("JKL-3456");
        automobileRepository.save(car4);

        Automobile car5 = new Automobile();
        car5.setId(UUID.randomUUID().toString());
        car5.setBrand("Ford");
        car5.setModel("Focus");
        car5.setYear(2022);
        car5.setDailyRate(105.0);
        car5.setAvailable(false);
        car5.setRegistration("MNO-2022");
        car5.setLicensePlate("MNO-7890");
        automobileRepository.save(car5);

        System.out.println("Sample automobiles created successfully!");
    }

    private void initializeCustomers() {
        if (customerRepository.count() > 0) {
            return;
        }

        Customer customer1 = new Customer();
        customer1.setId(UUID.randomUUID().toString());
        customer1.setName("João Silva");
        customer1.setEmailContact("joao.silva@email.com");
        customer1.setRg("12.345.678-9");
        customer1.setCpf("123.456.789-01");
        customer1.setAddress("Rua das Flores, 123 - São Paulo, SP");
        customer1.setProfession("Engenheiro");
        customer1.setCreatedAt(LocalDate.now());
        customer1.setUsername("joao.silva");
        customer1.setEmail("joao.silva@email.com");
        customerRepository.save(customer1);

        Customer customer2 = new Customer();
        customer2.setId(UUID.randomUUID().toString());
        customer2.setName("Maria Santos");
        customer2.setEmailContact("maria.santos@email.com");
        customer2.setRg("98.765.432-1");
        customer2.setCpf("987.654.321-09");
        customer2.setAddress("Avenida Paulista, 456 - São Paulo, SP");
        customer2.setProfession("Advogada");
        customer2.setCreatedAt(LocalDate.now());
        customer2.setUsername("maria.santos");
        customer2.setEmail("maria.santos@email.com");
        customerRepository.save(customer2);

        Customer customer3 = new Customer();
        customer3.setId(UUID.randomUUID().toString());
        customer3.setName("Pedro Lima");
        customer3.setEmailContact("pedro.lima@email.com");
        customer3.setRg("11.222.333-4");
        customer3.setCpf("111.222.333-44");
        customer3.setAddress("Rua do Comércio, 789 - Rio de Janeiro, RJ");
        customer3.setProfession("Designer");
        customer3.setCreatedAt(LocalDate.now());
        customer3.setUsername("pedro.lima");
        customer3.setEmail("pedro.lima@email.com");
        customerRepository.save(customer3);

        System.out.println("Sample customers created successfully!");
    }

    private void initializeUsers() {
        if (customerRepository.count() > 0) {
            return;
        }

        // Cliente
        Customer customer = new Customer();
        customer.setId(UUID.randomUUID().toString());
        customer.setUsername("cliente.teste");
        customer.setEmail("cliente@teste.com");
        customer.setPassword(passwordEncoder.encode("123456"));
        customer.setName("João Cliente");
        customer.setEmailContact("cliente@teste.com");
        customer.setRg("12.345.678-9");
        customer.setCpf("123.456.789-01");
        customer.setAddress("Rua das Flores, 123");
        customer.setProfession("Engenheiro");
        customer.setCreatedAt(LocalDate.now());
        customerRepository.save(customer);

        // Agente Empresa
        CompanyAgent companyAgent = new CompanyAgent();
        companyAgent.setId(UUID.randomUUID().toString());
        companyAgent.setUsername("agente.empresa");
        companyAgent.setEmail("agente@teste.com");
        companyAgent.setPassword(passwordEncoder.encode("123456"));
        companyAgent.setCorporateReason("Empresa de Aluguel de Carros LTDA");
        companyAgent.setCnpj("12.345.678/0001-90");
        companyAgent.setCreatedAt(LocalDate.now());
        companyAgentRepository.save(companyAgent);

        // Agente Banco
        BankAgent bankAgent = new BankAgent();
        bankAgent.setId(UUID.randomUUID().toString());
        bankAgent.setUsername("agente.banco");
        bankAgent.setEmail("banco@teste.com");
        bankAgent.setPassword(passwordEncoder.encode("123456"));
        bankAgent.setCorporateReason("Banco de Crédito LTDA");
        bankAgent.setCnpj("98.765.432/0001-10");
        bankAgent.setCreatedAt(LocalDate.now());
        bankAgentRepository.save(bankAgent);

        // Banco
        Bank bank = new Bank();
        bank.setId(UUID.randomUUID().toString());
        bank.setUsername("banco.sistema");
        bank.setEmail("banco.sistema@teste.com");
        bank.setPassword(passwordEncoder.encode("123456"));
        bank.setBankCode("001");
        bank.setCreatedAt(LocalDate.now());
        bankRepository.save(bank);

        System.out.println("Sample users with roles created successfully!");
    }
}