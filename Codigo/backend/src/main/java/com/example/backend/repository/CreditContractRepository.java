package com.example.backend.repository;

import com.example.backend.model.CreditContract;
import com.example.backend.model.RentalRequest;
import com.example.backend.model.Customer;
import com.example.backend.model.Bank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface CreditContractRepository extends JpaRepository<CreditContract, String> {

    // Buscar contratos de crédito por cliente
    List<CreditContract> findByRentalRequestCustomer(Customer customer);

    // Buscar contratos de crédito por banco
    List<CreditContract> findByGrantingBank(Bank bank);

    // Buscar contratos por status
    List<CreditContract> findByStatus(String status);

    // Buscar contratos concedidos em um período
    List<CreditContract> findByGrantDateBetween(LocalDate startDate, LocalDate endDate);

    // Verificar se existe contrato de crédito para um pedido
    boolean existsByRentalRequest(RentalRequest rentalRequest);

    // Buscar contrato de crédito por pedido de aluguel
    Optional<CreditContract> findByRentalRequest(RentalRequest rentalRequest);

    // Buscar contratos por faixa de valor
    List<CreditContract> findByValueBetween(Double minValue, Double maxValue);

    // Buscar contratos por taxa de juros
    List<CreditContract> findByInterestRateLessThanEqual(Double maxRate);

    // Buscar contratos por prazo
    List<CreditContract> findByTermLessThanEqual(Integer maxTerm);

    // Query para buscar contratos que vencem em breve
    @Query(value = "SELECT * FROM credit_contract WHERE status = 'ATIVO' AND DATEADD('MONTH', term, grant_date) <= :futureDate", nativeQuery = true)
    List<CreditContract> findContractsExpiringBefore(@Param("futureDate") LocalDate futureDate);

    // Query para calcular valor total de créditos por banco
    @Query("SELECT cc.grantingBank, SUM(cc.value) " +
            "FROM CreditContract cc " +
            "WHERE cc.status = 'ATIVO' " +
            "GROUP BY cc.grantingBank")
    List<Object[]> findActiveCreditByBank();

    // Query para buscar contratos por cliente e status
    List<CreditContract> findByRentalRequestCustomerAndStatus(Customer customer, String status);

    // Query customizada para análise de risco
    @Query("SELECT cc FROM CreditContract cc " +
            "WHERE cc.rentalRequest.customer = :customer " +
            "AND cc.status = 'ATIVO' " +
            "AND cc.value > :minValue")
    List<CreditContract> findHighValueActiveCreditsByCustomer(
            @Param("customer") Customer customer,
            @Param("minValue") Double minValue);

    // Query para estatísticas de crédito por mês
    @Query("SELECT MONTH(cc.grantDate) as month, " +
            "COUNT(cc) as count, " +
            "SUM(cc.value) as totalValue, " +
            "AVG(cc.interestRate) as avgRate " +
            "FROM CreditContract cc " +
            "WHERE YEAR(cc.grantDate) = :year " +
            "GROUP BY MONTH(cc.grantDate) " +
            "ORDER BY month")
    List<Object[]> findCreditStatsByMonth(@Param("year") int year);
}