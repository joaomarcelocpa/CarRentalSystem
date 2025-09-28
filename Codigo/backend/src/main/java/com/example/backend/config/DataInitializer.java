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

        // Carros econômicos
        createAutomobile("Toyota", "Corolla", 2023, "ABC-1234", "TOY-2023", 150.0, true);
        createAutomobile("Honda", "Civic", 2023, "DEF-5678", "HON-2023", 160.0, true);
        createAutomobile("Volkswagen", "Jetta", 2022, "GHI-9012", "VW-2022", 140.0, true);
        createAutomobile("Hyundai", "Elantra", 2023, "JKL-3456", "HYN-2023", 145.0, true);
        createAutomobile("Ford", "Focus", 2022, "MNO-7890", "FOR-2022", 135.0, false);
        
        // Carros médios
        createAutomobile("Toyota", "Camry", 2023, "PQR-1234", "CAM-2023", 200.0, true);
        createAutomobile("Honda", "Accord", 2023, "STU-5678", "ACC-2023", 210.0, true);
        createAutomobile("Nissan", "Altima", 2022, "VWX-9012", "ALT-2022", 190.0, true);
        createAutomobile("Chevrolet", "Malibu", 2023, "YZA-3456", "MAL-2023", 185.0, true);
        
        // SUVs
        createAutomobile("Toyota", "RAV4", 2023, "BCD-7890", "RAV-2023", 250.0, true);
        createAutomobile("Honda", "CR-V", 2023, "EFG-1234", "CRV-2023", 260.0, true);
        createAutomobile("Ford", "Escape", 2022, "HIJ-5678", "ESC-2022", 240.0, true);
        createAutomobile("Nissan", "Rogue", 2023, "KLM-9012", "ROG-2023", 245.0, false);
        
        // Carros de luxo
        createAutomobile("BMW", "320i", 2023, "NOP-3456", "BMW-2023", 350.0, true);
        createAutomobile("Mercedes-Benz", "C200", 2023, "QRS-7890", "MB-2023", 380.0, true);
        createAutomobile("Audi", "A4", 2023, "TUV-1234", "AUD-2023", 360.0, true);
        
        // Carros compactos
        createAutomobile("Fiat", "Argo", 2023, "WXY-5678", "ARG-2023", 120.0, true);
        createAutomobile("Renault", "Logan", 2022, "ZAB-9012", "LOG-2022", 110.0, true);
        createAutomobile("Peugeot", "208", 2023, "CDE-3456", "208-2023", 125.0, true);

        System.out.println("Sample automobiles created successfully!");
    }

    private void createAutomobile(String brand, String model, int year, String licensePlate, 
                                String registration, double dailyRate, boolean available) {
        Automobile automobile = new Automobile();
        automobile.setId(UUID.randomUUID().toString());
        automobile.setBrand(brand);
        automobile.setModel(model);
        automobile.setYear(year);
        automobile.setLicensePlate(licensePlate);
        automobile.setRegistration(registration);
        automobile.setDailyRate(dailyRate);
        automobile.setAvailable(available);
        automobile.setCreatedAt(LocalDate.now());
        automobileRepository.save(automobile);
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
        // Check if test users already exist by looking for specific usernames
        if (customerRepository.existsByUsername("cliente.teste")) {
            return;
        }

        // CLIENTES DE TESTE
        createCustomer("cliente.teste", "cliente@teste.com", "João Cliente", 
                      "12.345.678-9", "123.456.789-01", "Rua das Flores, 123", "Engenheiro");
        createCustomer("maria.cliente", "maria@teste.com", "Maria Cliente", 
                      "98.765.432-1", "987.654.321-09", "Avenida Paulista, 456", "Advogada");
        createCustomer("pedro.cliente", "pedro@teste.com", "Pedro Cliente", 
                      "11.222.333-4", "111.222.333-44", "Rua do Comércio, 789", "Designer");

        // AGENTES DE EMPRESA
        createCompanyAgent("agente.empresa", "agente@teste.com", 
                          "Empresa de Aluguel de Carros LTDA", "12.345.678/0001-90");
        createCompanyAgent("agente.empresa2", "agente2@teste.com", 
                          "Locadora Premium LTDA", "23.456.789/0001-01");
        createCompanyAgent("agente.empresa3", "agente3@teste.com", 
                          "Auto Rental Express LTDA", "34.567.890/0001-12");

        // AGENTES DE BANCO
        createBankAgent("agente.banco", "banco@teste.com", 
                       "Banco de Crédito LTDA", "98.765.432/0001-10");
        createBankAgent("agente.banco2", "banco2@teste.com", 
                       "Banco Financeiro S/A", "87.654.321/0001-21");
        createBankAgent("agente.banco3", "banco3@teste.com", 
                       "Banco CrediCar S/A", "76.543.210/0001-32");

        // BANCOS DO SISTEMA
        createBank("banco.sistema", "banco.sistema@teste.com", "001", "Banco Central do Sistema");
        createBank("banco.principal", "banco.principal@teste.com", "002", "Banco Principal");
        createBank("banco.secundario", "banco.secundario@teste.com", "003", "Banco Secundário");

        System.out.println("Sample users with roles created successfully!");
    }

    private void createCustomer(String username, String email, String name, String rg, 
                              String cpf, String address, String profession) {
        Customer customer = new Customer();
        customer.setId(UUID.randomUUID().toString());
        customer.setUsername(username);
        customer.setEmail(email);
        customer.setPassword(passwordEncoder.encode("123456"));
        customer.setName(name);
        customer.setEmailContact(email);
        customer.setRg(rg);
        customer.setCpf(cpf);
        customer.setAddress(address);
        customer.setProfession(profession);
        customer.setCreatedAt(LocalDate.now());
        customerRepository.save(customer);
    }

    private void createCompanyAgent(String username, String email, String corporateReason, String cnpj) {
        CompanyAgent companyAgent = new CompanyAgent();
        companyAgent.setId(UUID.randomUUID().toString());
        companyAgent.setUsername(username);
        companyAgent.setEmail(email);
        companyAgent.setPassword(passwordEncoder.encode("123456"));
        companyAgent.setCorporateReason(corporateReason);
        companyAgent.setCnpj(cnpj);
        companyAgent.setCreatedAt(LocalDate.now());
        companyAgentRepository.save(companyAgent);
    }

    private void createBankAgent(String username, String email, String corporateReason, String cnpj) {
        BankAgent bankAgent = new BankAgent();
        bankAgent.setId(UUID.randomUUID().toString());
        bankAgent.setUsername(username);
        bankAgent.setEmail(email);
        bankAgent.setPassword(passwordEncoder.encode("123456"));
        bankAgent.setCorporateReason(corporateReason);
        bankAgent.setCnpj(cnpj);
        bankAgent.setCreatedAt(LocalDate.now());
        bankAgentRepository.save(bankAgent);
    }

    private void createBank(String username, String email, String bankCode, String bankName) {
        Bank bank = new Bank();
        bank.setId(UUID.randomUUID().toString());
        bank.setUsername(username);
        bank.setEmail(email);
        bank.setPassword(passwordEncoder.encode("123456"));
        bank.setBankCode(bankCode);
        bank.setCreatedAt(LocalDate.now());
        bankRepository.save(bank);
    }
}