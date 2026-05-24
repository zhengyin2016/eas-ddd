package com.eas.crm.domain.opportunity.service;

import com.eas.crm.domain.opportunity.Opportunity;
import com.eas.crm.domain.opportunity.OpportunityRepository;
import com.eas.crm.domain.opportunity.OpportunityStage;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class OpportunityDomainService {

    private final OpportunityRepository opportunityRepository;

    public OpportunityDomainService(OpportunityRepository opportunityRepository) {
        this.opportunityRepository = opportunityRepository;
    }

    public double calculateOverallWinRate(OpportunityStage stage) {
        List<Opportunity> opportunities = opportunityRepository.findByStage(stage);

        if (opportunities.isEmpty()) {
            return 0.0;
        }

        long wonCount = opportunities.stream()
                .filter(Opportunity::isWon)
                .count();

        return BigDecimal.valueOf(wonCount)
                .divide(BigDecimal.valueOf(opportunities.size()), 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .doubleValue();
    }

    public double calculateConversionRate(OpportunityStage fromStage, OpportunityStage toStage) {
        List<Opportunity> fromOpportunities = opportunityRepository.findByStage(fromStage);
        List<Opportunity> toOpportunities = opportunityRepository.findByStage(toStage);

        if (fromOpportunities.isEmpty()) {
            return 0.0;
        }

        return BigDecimal.valueOf(toOpportunities.size())
                .divide(BigDecimal.valueOf(fromOpportunities.size()), 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .doubleValue();
    }

    public double calculateAverageSalesCycle() {
        List<Opportunity> allOpportunities = opportunityRepository.findAll();

        List<Opportunity> wonOpportunities = allOpportunities.stream()
                .filter(Opportunity::isWon)
                .filter(o -> o.getActualAmount() != null)
                .toList();

        if (wonOpportunities.isEmpty()) {
            return 0.0;
        }

        long totalDays = wonOpportunities.stream()
                .mapToLong(o -> java.time.temporal.ChronoUnit.DAYS.between(
                        o.getCreatedAt().toLocalDate(),
                        o.getUpdatedAt().toLocalDate()))
                .sum();

        return (double) totalDays / wonOpportunities.size();
    }
}
