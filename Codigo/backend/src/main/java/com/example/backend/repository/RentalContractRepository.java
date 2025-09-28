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

    // Buscar contratos por cliente
    List<RentalContract> findByRentalRequestCustomer(Customer customer);

    // Buscar contratos ativos (implementação customizada)
    @Query("SELECT rc FROM RentalContract rc WHERE rc.status = 'ATIVO' AND " +
            "rc.startDate <= :currentDate AND rc.endDate >= :currentDate")
    List<RentalContract> findActiveContracts(@Param("currentDate") LocalDate currentDate);

    // Método wrapper para facilitar o uso
    default List<RentalContract> findActiveContracts() {
        return findActiveContracts(LocalDate.now());
    }

    // Buscar contratos que expiram em breve
    @Query("SELECT rc FROM RentalContract rc WHERE rc.status = 'ATIVO' AND " +
            "rc.endDate BETWEEN :startDate AND :endDate")
    List<RentalContract> findExpiringContracts(@Param("startDate") LocalDate startDate,
                                               @Param("endDate") LocalDate endDate);

    // Método wrapper para facilitar o uso
    default List<RentalContract> findExpiringContracts(int daysAhead) {
        LocalDate today = LocalDate.now();
        LocalDate futureDate = today.plusDays(daysAhead);
        return findExpiringContracts(today, futureDate);
    }

    // Buscar contratos por período de assinatura
    List<RentalContract> findBySigningDateBetween(LocalDate startDate, LocalDate endDate);

    // Verificar se existe contrato para um pedido específico
    boolean existsByRentalRequest(RentalRequest rentalRequest);

    // Buscar contrato por pedido de aluguel
    Optional<RentalContract> findByRentalRequest(RentalRequest rentalRequest);

    // Buscar contratos por valor mínimo
    List<RentalContract> findByValueGreaterThanEqual(Double minValue);

    // Buscar contratos que começam em uma data específica
    List<RentalContract> findByStartDate(LocalDate startDate);

    // Buscar contratos que terminam em uma data específica
    List<RentalContract> findByEndDate(LocalDate endDate);

    // Query customizada para buscar contratos com duração específica
    @Query(value = "SELECT * FROM rental_contract WHERE DATEDIFF(end_date, start_date) = :days", nativeQuery = true)
    List<RentalContract> findByDurationInDays(@Param("days") long days);

    // Query para buscar contratos por marca de veículo
    @Query("SELECT rc FROM RentalContract rc " +
            "WHERE rc.rentalRequest.automobile.brand = :brand")
    List<RentalContract> findByAutomobileBrand(@Param("brand") String brand);

    // Query para estatísticas de contratos por mês
    @Query("SELECT MONTH(rc.signingDate) as month, COUNT(rc) as count " +
            "FROM RentalContract rc " +
            "WHERE YEAR(rc.signingDate) = :year " +
            "GROUP BY MONTH(rc.signingDate) " +
            "ORDER BY month")
    List<Object[]> findContractStatsByMonth(@Param("year") int year);
}