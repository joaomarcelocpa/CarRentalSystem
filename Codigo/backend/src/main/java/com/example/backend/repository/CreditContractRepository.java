package com.example.backend.repository;

import com.example.backend.model.CreditContract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CreditContractRepository extends JpaRepository<CreditContract, String> {

    // Busca o contrato de crédito de um cliente com um banco específico
    @Query("SELECT c FROM CreditContract c WHERE c.customer.id = :customerId AND c.bankAgent.id = :bankAgentId AND c.status = 'ACTIVE'")
    Optional<CreditContract> findByCustomerAndBankAgent(@Param("customerId") String customerId,
                                                        @Param("bankAgentId") String bankAgentId);

    // Busca o contrato de crédito de um cliente (username) com um banco específico (username)
    @Query("SELECT c FROM CreditContract c WHERE c.customer.username = :customerUsername AND c.bankAgent.username = :bankAgentUsername AND c.status = 'ACTIVE'")
    Optional<CreditContract> findByCustomerUsernameAndBankAgentUsername(@Param("customerUsername") String customerUsername,
                                                                        @Param("bankAgentUsername") String bankAgentUsername);

    // Lista todos os contratos de crédito gerenciados por um banco
    @Query("SELECT c FROM CreditContract c WHERE c.bankAgent.id = :bankAgentId ORDER BY c.updatedAt DESC")
    List<CreditContract> findAllByBankAgentId(@Param("bankAgentId") String bankAgentId);

    // Lista todos os contratos de crédito gerenciados por um banco (por username)
    @Query("SELECT c FROM CreditContract c WHERE c.bankAgent.username = :bankAgentUsername ORDER BY c.updatedAt DESC")
    List<CreditContract> findAllByBankAgentUsername(@Param("bankAgentUsername") String bankAgentUsername);

    // Lista todos os contratos de crédito de um cliente
    @Query("SELECT c FROM CreditContract c WHERE c.customer.id = :customerId ORDER BY c.updatedAt DESC")
    List<CreditContract> findAllByCustomerId(@Param("customerId") String customerId);

    // Verifica se já existe um contrato ativo entre cliente e banco
    @Query("SELECT COUNT(c) > 0 FROM CreditContract c WHERE c.customer.id = :customerId AND c.bankAgent.id = :bankAgentId AND c.status = 'ACTIVE'")
    boolean existsActiveContractByCustomerAndBankAgent(@Param("customerId") String customerId,
                                                       @Param("bankAgentId") String bankAgentId);
}