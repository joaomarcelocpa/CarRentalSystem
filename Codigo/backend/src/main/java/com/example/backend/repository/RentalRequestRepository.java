package com.example.backend.repository;

import com.example.backend.model.RentalRequest;
import com.example.backend.model.enums.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RentalRequestRepository extends JpaRepository<RentalRequest, String> {

    List<RentalRequest> findByStatus(RequestStatus status);

    @Query("SELECT r FROM RentalRequest r WHERE r.status = 'PENDING' ORDER BY r.createdAt DESC")
    List<RentalRequest> findAllPending();

    @Query("SELECT r FROM RentalRequest r WHERE r.customer.id = :customerId ORDER BY r.createdAt DESC")
    List<RentalRequest> findByCustomerId(@Param("customerId") String customerId);

    @Query("SELECT r FROM RentalRequest r WHERE r.customer.username = :username ORDER BY r.createdAt DESC")
    List<RentalRequest> findByCustomerUsername(@Param("username") String username);

    @Query("SELECT r FROM RentalRequest r WHERE r.processedByAgentId = :agentId ORDER BY r.processedAt DESC")
    List<RentalRequest> findByProcessedByAgentId(@Param("agentId") String agentId);

    @Query("SELECT r FROM RentalRequest r WHERE r.processedByAgentUsername = :username ORDER BY r.processedAt DESC")
    List<RentalRequest> findByProcessedByAgentUsername(@Param("username") String username);

    @Query("SELECT r FROM RentalRequest r WHERE r.automobile.id = :automobileId ORDER BY r.createdAt DESC")
    List<RentalRequest> findByAutomobileId(@Param("automobileId") String automobileId);

    @Query("SELECT r FROM RentalRequest r WHERE r.automobile.createdByAgentUsername = :agentUsername ORDER BY r.createdAt DESC")
    List<RentalRequest> findRequestsForAgentAutomobiles(@Param("agentUsername") String agentUsername);

    @Query("SELECT COUNT(r) > 0 FROM RentalRequest r WHERE r.automobile.id = :automobileId AND r.status IN ('APPROVED', 'ACTIVE')")
    boolean existsActiveRequestForAutomobile(@Param("automobileId") String automobileId);
}