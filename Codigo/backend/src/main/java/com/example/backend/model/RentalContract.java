package com.example.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Entity
@Table(name = "rental_contracts")
public class RentalContract {

    @Id
    private String id;

    @NotNull
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @NotNull
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @NotNull
    @Min(0)
    @Column(name = "contract_value", nullable = false)
    private Double value;

    @NotNull
    @Column(name = "signing_date", nullable = false)
    private LocalDate signingDate;

    @Lob
    @Column(name = "terms", columnDefinition = "TEXT")
    private String terms;

    @Column(name = "status", nullable = false)
    private String status = "ATIVO";

    @Column(name = "renewal_count")
    private Integer renewalCount = 0;

    @Column(name = "observations", columnDefinition = "TEXT")
    private String observations;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rental_request_id", unique = true)
    private RentalRequest rentalRequest;

    public RentalContract() {}

    public RentalContract(LocalDate startDate, LocalDate endDate, Double value,
                          RentalRequest rentalRequest) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.value = value;
        this.rentalRequest = rentalRequest;
        this.signingDate = LocalDate.now();
    }

    /**
     * Calcula o valor total do contrato
     * Conforme método do diagrama: calculateTotalValue(): Double
     */
    public Double calculateTotalValue() {
        return value != null ? value : 0.0;
    }

    /**
     * Calcula a duração do contrato em dias
     */
    public Long getDurationInDays() {
        if (startDate == null || endDate == null) return 0L;
        return ChronoUnit.DAYS.between(startDate, endDate);
    }

    /**
     * Calcula a duração do contrato em meses
     */
    public Long getDurationInMonths() {
        if (startDate == null || endDate == null) return 0L;
        long days = ChronoUnit.DAYS.between(startDate, endDate);
        return Math.max(1L, days / 30);
    }

    /**
     * Verifica se o contrato está ativo no momento atual
     */
    public boolean isCurrentlyActive() {
        LocalDate today = LocalDate.now();
        return "ATIVO".equals(status) &&
                !today.isBefore(startDate) &&
                !today.isAfter(endDate);
    }

    /**
     * Verifica se o contrato já expirou
     */
    public boolean isExpired() {
        return LocalDate.now().isAfter(endDate);
    }

    /**
     * Calcula quantos dias restam no contrato
     */
    public Long getDaysRemaining() {
        LocalDate today = LocalDate.now();
        if (today.isAfter(endDate)) return 0L;
        return ChronoUnit.DAYS.between(today, endDate);
    }

    /**
     * Renova o contrato para uma nova data de término
     * Conforme método do diagrama: renew(newEndDate: Date): void
     */
    public void renew(LocalDate newEndDate) {
        if (newEndDate.isBefore(this.endDate)) {
            throw new IllegalArgumentException("Nova data deve ser posterior à atual");
        }

        this.endDate = newEndDate;
        this.renewalCount = (this.renewalCount != null ? this.renewalCount : 0) + 1;
        this.status = "RENOVADO";

        if (rentalRequest != null && rentalRequest.getAutomobile() != null) {
            Long totalDays = getDurationInDays();
            Double dailyRate = rentalRequest.getAutomobile().getDailyRate();
            if (dailyRate != null && totalDays != null) {
                this.value = dailyRate * totalDays;
            }
        }
    }

    /**
     * Finaliza o contrato (nome diferente de finalize para evitar conflito)
     * Conforme método do diagrama: finalize(): void
     */
    public void finalizeContract() {
        this.status = "FINALIZADO";

        if (rentalRequest != null && rentalRequest.getAutomobile() != null) {
            rentalRequest.getAutomobile().setAvailable(true);
        }
    }

    /**
     * Cancela o contrato
     */
    public void cancelContract(String reason) {
        this.status = "CANCELADO";
        this.observations = (this.observations != null ? this.observations + "\n" : "") +
                "Contrato cancelado em " + LocalDate.now() + ". Motivo: " + reason;

        if (rentalRequest != null && rentalRequest.getAutomobile() != null) {
            rentalRequest.getAutomobile().setAvailable(true);
        }
    }

    /**
     * Calcula o valor proporcional para cancelamento antecipado
     */
    public Double calculateProportionalValue() {
        if (startDate == null || endDate == null || value == null) return 0.0;

        LocalDate today = LocalDate.now();
        if (today.isBefore(startDate)) return 0.0;
        if (today.isAfter(endDate)) return value;

        long totalDays = ChronoUnit.DAYS.between(startDate, endDate);
        long usedDays = ChronoUnit.DAYS.between(startDate, today);

        return (value * usedDays) / totalDays;
    }

    // Getters e Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public Double getValue() { return value; }
    public void setValue(Double value) { this.value = value; }

    public LocalDate getSigningDate() { return signingDate; }
    public void setSigningDate(LocalDate signingDate) { this.signingDate = signingDate; }

    public String getTerms() { return terms; }
    public void setTerms(String terms) { this.terms = terms; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Integer getRenewalCount() { return renewalCount; }
    public void setRenewalCount(Integer renewalCount) { this.renewalCount = renewalCount; }

    public String getObservations() { return observations; }
    public void setObservations(String observations) { this.observations = observations; }

    public RentalRequest getRentalRequest() { return rentalRequest; }
    public void setRentalRequest(RentalRequest rentalRequest) { this.rentalRequest = rentalRequest; }
}
