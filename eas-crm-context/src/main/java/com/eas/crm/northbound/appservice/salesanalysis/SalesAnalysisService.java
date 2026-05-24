package com.eas.crm.northbound.appservice.salesanalysis;

import com.eas.crm.domain.opportunity.OpportunityRepository;
import com.eas.crm.domain.opportunity.OpportunityStage;
import com.eas.crm.domain.opportunity.service.OpportunityDomainService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class SalesAnalysisService {

    private final OpportunityRepository opportunityRepository;
    private final OpportunityDomainService opportunityDomainService;

    public SalesAnalysisService(OpportunityRepository opportunityRepository,
                               OpportunityDomainService opportunityDomainService) {
        this.opportunityRepository = opportunityRepository;
        this.opportunityDomainService = opportunityDomainService;
    }

    public SalesFunnelResponse analyzeSalesFunnel() {
        Map<String, StageStats> stageStatsMap = new HashMap<>();

        for (OpportunityStage stage : OpportunityStage.values()) {
            var opportunities = opportunityRepository.findByStage(stage);

            int count = opportunities.size();
            double totalAmount = opportunities.stream()
                    .mapToDouble(o -> o.getEstimatedAmount().amount().doubleValue())
                    .sum();

            stageStatsMap.put(stage.name(), new StageStats(count, totalAmount));
        }

        double conversionRate = opportunityDomainService.calculateConversionRate(
                OpportunityStage.INITIAL_CONTACT,
                OpportunityStage.CONTRACT_SIGNING
        );

        double avgSalesCycle = opportunityDomainService.calculateAverageSalesCycle();

        return new SalesFunnelResponse(stageStatsMap, conversionRate, avgSalesCycle);
    }

    public record SalesFunnelResponse(
            Map<String, StageStats> stageStats,
            double conversionRate,
            double averageSalesCycle
    ) {}

    public record StageStats(
            int count,
            double totalAmount
    ) {}
}
