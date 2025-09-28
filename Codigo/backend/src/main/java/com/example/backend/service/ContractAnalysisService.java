package com.example.backend.service;

import com.example.backend.model.*;
import com.example.backend.model.enums.RequestStatus;
import com.example.backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ContractAnalysisService {

    @Autowired
    private RentalContractRepository rentalContractRepository;

    @Autowired
    private CreditContractRepository creditContractRepository;

    @Autowired
    private RentalRequestRepository rentalRequestRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private AutomobileRepository automobileRepository;

    /**
     * Gera dashboard completo com estatísticas dos contratos
     */
    public Map<String, Object> generateContractDashboard() {
        Map<String, Object> dashboard = new HashMap<>();

        // Estatísticas de contratos de aluguel
        List<RentalContract> allRentalContracts = rentalContractRepository.findAll();
        List<RentalContract> activeRentalContracts = rentalContractRepository.findActiveContracts();
        List<RentalContract> expiringRentalContracts = rentalContractRepository.findExpiringContracts(30);

        dashboard.put("totalRentalContracts", allRentalContracts.size());
        dashboard.put("activeRentalContracts", activeRentalContracts.size());
        dashboard.put("expiringRentalContracts", expiringRentalContracts.size());
        dashboard.put("totalRentalRevenue", calculateTotalRentalRevenue(allRentalContracts));

        // Estatísticas de contratos de crédito
        List<CreditContract> allCreditContracts = creditContractRepository.findAll();
        List<CreditContract> activeCreditContracts = creditContractRepository.findByStatus("ATIVO");

        dashboard.put("totalCreditContracts", allCreditContracts.size());
        dashboard.put("activeCreditContracts", activeCreditContracts.size());
        dashboard.put("totalCreditValue", calculateTotalCreditValue(activeCreditContracts));
        dashboard.put("totalCreditInterest", calculateTotalCreditInterest(activeCreditContracts));

        // Estatísticas de pedidos
        List<RentalRequest> allRequests = rentalRequestRepository.findAll();
        dashboard.put("totalRequests", allRequests.size());
        dashboard.put("pendingRequests", countRequestsByStatus(allRequests, RequestStatus.UNDER_ANALYSIS));
        dashboard.put("approvedRequests", countRequestsByStatus(allRequests, RequestStatus.APPROVED));
        dashboard.put("rejectedRequests", countRequestsByStatus(allRequests, RequestStatus.REJECTED));

        // Análise de automóveis
        List<Automobile> allAutomobiles = automobileRepository.findAll();
        dashboard.put("totalAutomobiles", allAutomobiles.size());
        dashboard.put("availableAutomobiles", countAvailableAutomobiles(allAutomobiles));
        dashboard.put("mostRequestedBrand", getMostRequestedBrand());

        dashboard.put("generatedAt", LocalDate.now());

        return dashboard;
    }

    /**
     * Gera relatório mensal detalhado
     */
    public Map<String, Object> generateMonthlyReport(int year, int month) {
        Map<String, Object> report = new HashMap<>();

        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.plusMonths(1).minusDays(1);

        // Contratos de aluguel do mês
        List<RentalContract> monthlyRentalContracts = rentalContractRepository
                .findBySigningDateBetween(startDate, endDate);

        // Contratos de crédito do mês
        List<CreditContract> monthlyCreditContracts = creditContractRepository
                .findByGrantDateBetween(startDate, endDate);

        report.put("period", month + "/" + year);
        report.put("rentalContractsCount", monthlyRentalContracts.size());
        report.put("creditContractsCount", monthlyCreditContracts.size());
        report.put("totalRentalRevenue", calculateTotalRentalRevenue(monthlyRentalContracts));
        report.put("totalCreditValue", calculateTotalCreditValue(monthlyCreditContracts));

        // Análise de performance
        report.put("averageContractValue", calculateAverageRentalValue(monthlyRentalContracts));
        report.put("averageCreditValue", calculateAverageCreditValue(monthlyCreditContracts));

        // Top clientes do mês
        report.put("topCustomers", getTopCustomersOfMonth(startDate, endDate));

        // Análise de automóveis mais alugados
        report.put("topAutomobiles", getTopAutomobilesOfMonth(startDate, endDate));

        return report;
    }

    /**
     * Análise de risco de crédito para cliente
     */
    public Map<String, Object> analyzeCreditRisk(String customerId) {
        Map<String, Object> analysis = new HashMap<>();

        Customer customer = customerRepository.findById(customerId).orElse(null);
        if (customer == null) {
            analysis.put("error", "Cliente não encontrado");
            return analysis;
        }

        List<CreditContract> customerCredits = creditContractRepository.findByRentalRequestCustomer(customer);
        List<RentalContract> customerRentals = rentalContractRepository.findByRentalRequestCustomer(customer);

        // Análise histórica
        analysis.put("totalCreditsUsed", customerCredits.size());
        analysis.put("totalRentalsCompleted", customerRentals.size());
        analysis.put("activeCredits", customerCredits.stream()
                .mapToInt(credit -> "ATIVO".equals(credit.getStatus()) ? 1 : 0).sum());

        // Análise financeira
        double totalCreditValue = customerCredits.stream()
                .filter(credit -> "ATIVO".equals(credit.getStatus()))
                .mapToDouble(CreditContract::getValue).sum();

        analysis.put("currentCreditExposure", totalCreditValue);
        analysis.put("averageCreditValue", calculateAverageCreditValue(customerCredits));

        // Score de risco (simplificado)
        int riskScore = calculateRiskScore(customer, customerCredits, customerRentals);
        analysis.put("riskScore", riskScore);
        analysis.put("riskLevel", getRiskLevel(riskScore));
        analysis.put("creditLimit", calculateRecommendedCreditLimit(riskScore));

        return analysis;
    }

    /**
     * Previsão de receita com base nos contratos ativos
     */
    public Map<String, Object> generateRevenueProjection(int monthsAhead) {
        Map<String, Object> projection = new HashMap<>();

        List<RentalContract> activeContracts = rentalContractRepository.findActiveContracts();
        List<CreditContract> activeCredits = creditContractRepository.findByStatus("ATIVO");

        double currentMonthlyRental = activeContracts.stream()
                .mapToDouble(contract -> {
                    Long durationMonths = contract.getDurationInMonths();
                    return durationMonths != null && durationMonths > 0 ?
                            contract.getValue() / durationMonths : 0.0;
                }).sum();

        double currentMonthlyCredit = activeCredits.stream()
                .mapToDouble(CreditContract::calculateInstallment).sum();

        List<Map<String, Object>> monthlyProjections = new ArrayList<>();

        for (int i = 1; i <= monthsAhead; i++) {
            Map<String, Object> monthProjection = new HashMap<>();
            LocalDate projectionDate = LocalDate.now().plusMonths(i);

            // Estimativa conservadora (redução gradual por contratos que expiram)
            double decayFactor = Math.pow(0.95, i); // 5% de redução por mês
            double projectedRental = currentMonthlyRental * decayFactor;
            double projectedCredit = currentMonthlyCredit * decayFactor;

            monthProjection.put("month", projectionDate.getMonthValue());
            monthProjection.put("year", projectionDate.getYear());
            monthProjection.put("projectedRentalRevenue", projectedRental);
            monthProjection.put("projectedCreditRevenue", projectedCredit);
            monthProjection.put("totalProjectedRevenue", projectedRental + projectedCredit);

            monthlyProjections.add(monthProjection);
        }

        projection.put("monthlyProjections", monthlyProjections);
        projection.put("totalProjectedRevenue", monthlyProjections.stream()
                .mapToDouble(p -> (Double) p.get("totalProjectedRevenue")).sum());

        projection.put("generatedAt", LocalDate.now());
        projection.put("projectionPeriod", monthsAhead + " meses");

        return projection;
    }

    /**
     * Análise de performance de automóveis
     */
    public Map<String, Object> analyzeAutomobilePerformance() {
        Map<String, Object> analysis = new HashMap<>();

        List<Automobile> allAutomobiles = automobileRepository.findAll();
        List<RentalContract> allContracts = rentalContractRepository.findAll();

        // Análise por marca
        Map<String, List<RentalContract>> contractsByBrand = allContracts.stream()
                .filter(contract -> contract.getRentalRequest() != null &&
                        contract.getRentalRequest().getAutomobile() != null)
                .collect(Collectors.groupingBy(contract ->
                        contract.getRentalRequest().getAutomobile().getBrand()));

        Map<String, Object> brandAnalysis = new HashMap<>();
        for (Map.Entry<String, List<RentalContract>> entry : contractsByBrand.entrySet()) {
            Map<String, Object> brandStats = new HashMap<>();
            brandStats.put("contractCount", entry.getValue().size());
            brandStats.put("totalRevenue", entry.getValue().stream()
                    .mapToDouble(RentalContract::getValue).sum());
            brandStats.put("averageContractValue", entry.getValue().stream()
                    .mapToDouble(RentalContract::getValue).average().orElse(0.0));
            brandAnalysis.put(entry.getKey(), brandStats);
        }

        analysis.put("brandPerformance", brandAnalysis);

        // Top 10 automóveis mais lucrativos
        List<Map<String, Object>> topAutomobiles = allContracts.stream()
                .filter(contract -> contract.getRentalRequest() != null &&
                        contract.getRentalRequest().getAutomobile() != null)
                .collect(Collectors.groupingBy(contract ->
                        contract.getRentalRequest().getAutomobile().getId()))
                .entrySet().stream()
                .map(entry -> {
                    Map<String, Object> autoStats = new HashMap<>();
                    Automobile auto = entry.getValue().get(0).getRentalRequest().getAutomobile();
                    autoStats.put("automobileId", entry.getKey());
                    autoStats.put("brand", auto.getBrand());
                    autoStats.put("model", auto.getModel());
                    autoStats.put("year", auto.getYear());
                    autoStats.put("contractCount", entry.getValue().size());
                    autoStats.put("totalRevenue", entry.getValue().stream()
                            .mapToDouble(RentalContract::getValue).sum());
                    return autoStats;
                })
                .sorted((a, b) -> Double.compare((Double) b.get("totalRevenue"), (Double) a.get("totalRevenue")))
                .limit(10)
                .collect(Collectors.toList());

        analysis.put("topAutomobiles", topAutomobiles);

        // Taxa de utilização
        long totalAutomobiles = allAutomobiles.size();
        long availableAutomobiles = allAutomobiles.stream()
                .mapToLong(auto -> auto.isAvailable() ? 1 : 0).sum();

        double utilizationRate = totalAutomobiles > 0 ?
                ((double) (totalAutomobiles - availableAutomobiles) / totalAutomobiles) * 100 : 0.0;

        analysis.put("utilizationRate", utilizationRate);
        analysis.put("totalAutomobiles", totalAutomobiles);
        analysis.put("activeRentals", totalAutomobiles - availableAutomobiles);

        return analysis;
    }

    // Métodos auxiliares privados
    private double calculateTotalRentalRevenue(List<RentalContract> contracts) {
        return contracts.stream().mapToDouble(RentalContract::getValue).sum();
    }

    private double calculateTotalCreditValue(List<CreditContract> credits) {
        return credits.stream().mapToDouble(CreditContract::getValue).sum();
    }

    private double calculateTotalCreditInterest(List<CreditContract> credits) {
        return credits.stream()
                .mapToDouble(credit -> {
                    double installment = credit.calculateInstallment();
                    int term = credit.getTerm() != null ? credit.getTerm() : 0;
                    double value = credit.getValue() != null ? credit.getValue() : 0.0;
                    return (installment * term) - value;
                })
                .sum();
    }

    private long countRequestsByStatus(List<RentalRequest> requests, RequestStatus status) {
        return requests.stream().filter(req -> req.getStatus() == status).count();
    }

    private long countAvailableAutomobiles(List<Automobile> automobiles) {
        return automobiles.stream().filter(Automobile::isAvailable).count();
    }

    private String getMostRequestedBrand() {
        List<RentalRequest> allRequests = rentalRequestRepository.findAll();
        Map<String, Long> brandCounts = allRequests.stream()
                .filter(req -> req.getAutomobile() != null)
                .collect(Collectors.groupingBy(req -> req.getAutomobile().getBrand(),
                        Collectors.counting()));

        return brandCounts.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("N/A");
    }

    private double calculateAverageRentalValue(List<RentalContract> contracts) {
        return contracts.stream().mapToDouble(RentalContract::getValue).average().orElse(0.0);
    }

    private double calculateAverageCreditValue(List<CreditContract> credits) {
        return credits.stream().mapToDouble(CreditContract::getValue).average().orElse(0.0);
    }

    private List<Map<String, Object>> getTopCustomersOfMonth(LocalDate startDate, LocalDate endDate) {
        List<RentalContract> monthlyContracts = rentalContractRepository
                .findBySigningDateBetween(startDate, endDate);

        Map<Customer, Double> customerRevenue = new HashMap<>();
        for (RentalContract contract : monthlyContracts) {
            if (contract.getRentalRequest() != null && contract.getRentalRequest().getCustomer() != null) {
                Customer customer = contract.getRentalRequest().getCustomer();
                customerRevenue.merge(customer, contract.getValue(), Double::sum);
            }
        }

        return customerRevenue.entrySet().stream()
                .sorted(Map.Entry.<Customer, Double>comparingByValue().reversed())
                .limit(5)
                .map(entry -> {
                    Map<String, Object> customerData = new HashMap<>();
                    customerData.put("customerId", entry.getKey().getId());
                    customerData.put("customerName", entry.getKey().getName());
                    customerData.put("totalRevenue", entry.getValue());
                    return customerData;
                })
                .collect(Collectors.toList());
    }

    private List<Map<String, Object>> getTopAutomobilesOfMonth(LocalDate startDate, LocalDate endDate) {
        List<RentalContract> monthlyContracts = rentalContractRepository
                .findBySigningDateBetween(startDate, endDate);

        Map<Automobile, Integer> automobileUsage = new HashMap<>();
        for (RentalContract contract : monthlyContracts) {
            if (contract.getRentalRequest() != null && contract.getRentalRequest().getAutomobile() != null) {
                Automobile automobile = contract.getRentalRequest().getAutomobile();
                automobileUsage.merge(automobile, 1, Integer::sum);
            }
        }

        return automobileUsage.entrySet().stream()
                .sorted(Map.Entry.<Automobile, Integer>comparingByValue().reversed())
                .limit(5)
                .map(entry -> {
                    Map<String, Object> autoData = new HashMap<>();
                    autoData.put("automobileId", entry.getKey().getId());
                    autoData.put("brand", entry.getKey().getBrand());
                    autoData.put("model", entry.getKey().getModel());
                    autoData.put("usageCount", entry.getValue());
                    return autoData;
                })
                .collect(Collectors.toList());
    }

    private int calculateRiskScore(Customer customer, List<CreditContract> credits, List<RentalContract> rentals) {
        int score = 100; // Score inicial

        // Penalizar por créditos ativos em excesso
        long activeCredits = credits.stream().filter(c -> "ATIVO".equals(c.getStatus())).count();
        if (activeCredits > 2) score -= 20;
        else if (activeCredits > 1) score -= 10;

        // Penalizar por atrasos (verificar se há contratos em atraso)
        long overdueCredits = credits.stream()
                .filter(c -> "ATIVO".equals(c.getStatus()) && c.isOverdue()).count();
        score -= (int) (overdueCredits * 30);

        // Bonificar por histórico de contratos finalizados com sucesso
        long completedRentals = rentals.stream()
                .filter(r -> "FINALIZADO".equals(r.getStatus())).count();
        score += Math.min((int) (completedRentals * 5), 20);

        // Garantir que o score esteja entre 0 e 100
        return Math.max(0, Math.min(100, score));
    }

    private String getRiskLevel(int score) {
        if (score >= 80) return "BAIXO";
        if (score >= 60) return "MÉDIO";
        if (score >= 40) return "ALTO";
        return "MUITO ALTO";
    }

    private double calculateRecommendedCreditLimit(int riskScore) {
        if (riskScore >= 80) return 100000.0;
        if (riskScore >= 60) return 50000.0;
        if (riskScore >= 40) return 20000.0;
        return 5000.0;
    }
}