package com.example.backend.repository;

import com.example.backend.model.RentalContract;
import com.example.backend.model.RentalRequest;
import com.example.backend.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface RentalContractRepository extends JpaRepository<RentalContract, String> {

    List<RentalContract> findByRentalRequestCustomer(Customer customer);

    @Query("SELECT rc FROM RentalContract rc WHERE rc.status = 'ATIVO' AND " +
            "rc.startDate <= :currentDate AND rc.endDate >= :currentDate")
    List<RentalContract> findActiveContracts(@Param("currentDate") LocalDate currentDate);

    default List<RentalContract> findActiveContracts() {
        return findActiveContracts(LocalDate.now());
    }

    @Query("SELECT rc FROM RentalContract rc WHERE rc.status = 'ATIVO' AND " +
            "rc.endDate BETWEEN :startDate AND :endDate")
    List<RentalContract> findExpiringContracts(@Param("startDate") LocalDate startDate,
                                               @Param("endDate") LocalDate endDate);

    default List<RentalContract> findExpiringContracts(int daysAhead) {
        LocalDate today = LocalDate.now();
        LocalDate futureDate = today.plusDays(daysAhead);
        return findExpiringContracts(today, futureDate);
    }

    List<RentalContract> findBySigningDateBetween(LocalDate startDate, LocalDate endDate);

    boolean existsByRentalRequest(RentalRequest rentalRequest);

    Optional<RentalContract> findByRentalRequest(RentalRequest rentalRequest);

    List<RentalContract> findByValueGreaterThanEqual(Double minValue);

    List<RentalContract> findByStartDate(LocalDate startDate);

    List<RentalContract> findByEndDate(LocalDate endDate);

    @Query(value = "SELECT * FROM rental_contract WHERE DATEDIFF(end_date, start_date) = :days", nativeQuery = true)
    List<RentalContract> findByDurationInDays(@Param("days") long days);

    @Query("SELECT rc FROM RentalContract rc " +
            "WHERE rc.rentalRequest.automobile.brand = :brand")
    List<RentalContract> findByAutomobileBrand(@Param("brand") String brand);

    @Query("SELECT MONTH(rc.signingDate) as month, COUNT(rc) as count " +
            "FROM RentalContract rc " +
            "WHERE YEAR(rc.signingDate) = :year " +
            "GROUP BY MONTH(rc.signingDate) " +
            "ORDER BY month")
    List<Object[]> findContractStatsByMonth(@Param("year") int year);
}