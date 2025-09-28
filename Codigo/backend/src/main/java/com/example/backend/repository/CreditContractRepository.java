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

    List<CreditContract> findByRentalRequestCustomer(Customer customer);

    List<CreditContract> findByGrantingBank(Bank bank);

    List<CreditContract> findByStatus(String status);

    List<CreditContract> findByGrantDateBetween(LocalDate startDate, LocalDate endDate);

    boolean existsByRentalRequest(RentalRequest rentalRequest);

    Optional<CreditContract> findByRentalRequest(RentalRequest rentalRequest);

    List<CreditContract> findByValueBetween(Double minValue, Double maxValue);

    List<CreditContract> findByInterestRateLessThanEqual(Double maxRate);

    List<CreditContract> findByTermLessThanEqual(Integer maxTerm);

    @Query(value = "SELECT * FROM credit_contract WHERE status = 'ATIVO' AND DATEADD('MONTH', term, grant_date) <= :futureDate", nativeQuery = true)
    List<CreditContract> findContractsExpiringBefore(@Param("futureDate") LocalDate futureDate);

    @Query("SELECT cc.grantingBank, SUM(cc.value) " +
            "FROM CreditContract cc " +
            "WHERE cc.status = 'ATIVO' " +
            "GROUP BY cc.grantingBank")
    List<Object[]> findActiveCreditByBank();


    List<CreditContract> findByRentalRequestCustomerAndStatus(Customer customer, String status);

    @Query("SELECT cc FROM CreditContract cc " +
            "WHERE cc.rentalRequest.customer = :customer " +
            "AND cc.status = 'ATIVO' " +
            "AND cc.value > :minValue")
    List<CreditContract> findHighValueActiveCreditsByCustomer(
            @Param("customer") Customer customer,
            @Param("minValue") Double minValue);

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