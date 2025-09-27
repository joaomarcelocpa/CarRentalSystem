package com.example.backend.security;

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
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class MultiRepositoryUserDetailsService implements UserDetailsService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CompanyAgentRepository companyAgentRepository;

    @Autowired
    private BankAgentRepository bankAgentRepository;

    @Autowired
    private BankRepository bankRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Object user = findUserByUsername(username);

        return new org.springframework.security.core.userdetails.User(
                getUsername(user),
                getPassword(user),
                true,
                true,
                true,
                true,
                getAuthorities(getRole(user))
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

        throw new UsernameNotFoundException("Usuário não encontrado: " + username);
    }

    private String getUsername(Object user) {
        if (user instanceof Customer customer) return customer.getUsername();
        if (user instanceof CompanyAgent agent) return agent.getUsername();
        if (user instanceof BankAgent agent) return agent.getUsername();
        if (user instanceof Bank bank) return bank.getUsername();
        throw new IllegalArgumentException("Tipo de usuário não suportado");
    }

    private String getPassword(Object user) {
        if (user instanceof Customer customer) return customer.getPassword();
        if (user instanceof CompanyAgent agent) return agent.getPassword();
        if (user instanceof BankAgent agent) return agent.getPassword();
        if (user instanceof Bank bank) return bank.getPassword();
        throw new IllegalArgumentException("Tipo de usuário não suportado");
    }

    private UserRole getRole(Object user) {
        if (user instanceof Customer customer) return customer.getRole();
        if (user instanceof CompanyAgent agent) return agent.getRole();
        if (user instanceof BankAgent agent) return agent.getRole();
        if (user instanceof Bank bank) return bank.getRole();
        throw new IllegalArgumentException("Tipo de usuário não suportado");
    }

    private Collection<? extends GrantedAuthority> getAuthorities(UserRole role) {
        List<GrantedAuthority> authorities = new ArrayList<>();

        // Adiciona a role como autoridade
        authorities.add(new SimpleGrantedAuthority("ROLE_" + role.name()));

        // Adiciona permissões específicas baseadas na role
        switch (role) {
            case CUSTOMER:
                authorities.add(new SimpleGrantedAuthority("PERM_CUSTOMER_READ"));
                authorities.add(new SimpleGrantedAuthority("PERM_CUSTOMER_WRITE"));
                authorities.add(new SimpleGrantedAuthority("PERM_RENTAL_REQUEST_CREATE"));
                authorities.add(new SimpleGrantedAuthority("PERM_RENTAL_REQUEST_READ_OWN"));
                break;
            case AGENT_COMPANY:
                authorities.add(new SimpleGrantedAuthority("PERM_AGENT_READ"));
                authorities.add(new SimpleGrantedAuthority("PERM_AGENT_WRITE"));
                authorities.add(new SimpleGrantedAuthority("PERM_RENTAL_REQUEST_READ_ALL"));
                authorities.add(new SimpleGrantedAuthority("PERM_RENTAL_REQUEST_UPDATE"));
                authorities.add(new SimpleGrantedAuthority("PERM_AUTOMOBILE_MANAGE"));
                break;
            case AGENT_BANK:
                authorities.add(new SimpleGrantedAuthority("PERM_AGENT_READ"));
                authorities.add(new SimpleGrantedAuthority("PERM_AGENT_WRITE"));
                authorities.add(new SimpleGrantedAuthority("PERM_RENTAL_REQUEST_READ_ALL"));
                authorities.add(new SimpleGrantedAuthority("PERM_RENTAL_REQUEST_UPDATE"));
                authorities.add(new SimpleGrantedAuthority("PERM_CREDIT_CONTRACT_MANAGE"));
                authorities.add(new SimpleGrantedAuthority("PERM_FINANCIAL_ANALYSIS"));
                break;
        }

        return authorities;
    }
}