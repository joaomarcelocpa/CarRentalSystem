package com.example.backend.security;

import com.example.backend.model.User;
import com.example.backend.model.enums.UserRole;
import com.example.backend.repository.UserRepository;
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
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + username));

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                true,
                true,
                true,
                true,
                getAuthorities(user.getRole())
        );
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
                authorities.add(new SimpleGrantedAuthority("PERM_AUTOMOBILE_MANAGE")); // ADICIONADO
                break;
        }

        return authorities;
    }
}