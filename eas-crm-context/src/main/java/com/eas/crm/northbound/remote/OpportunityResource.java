package com.eas.crm.northbound.remote;

import com.eas.crm.domain.opportunity.OpportunityStage;
import com.eas.crm.message.CreateOpportunityRequest;
import com.eas.crm.message.OpportunityResponse;
import com.eas.crm.northbound.appservice.OpportunityAppService;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/opportunities")
public class OpportunityResource {

    private final OpportunityAppService opportunityAppService;

    public OpportunityResource(OpportunityAppService opportunityAppService) {
        this.opportunityAppService = opportunityAppService;
    }

    @PostMapping
    public OpportunityResponse createOpportunity(@RequestBody CreateOpportunityRequest request) {
        return opportunityAppService.createOpportunity(request);
    }

    @GetMapping("/{id}")
    public OpportunityResponse getOpportunity(@PathVariable String id) {
        return opportunityAppService.getOpportunity(id);
    }

    @PostMapping("/{id}/advance-stage")
    public OpportunityResponse advanceStage(
            @PathVariable String id,
            @RequestBody AdvanceStageRequest request) {
        return opportunityAppService.advanceStage(id, request.stage());
    }

    @PostMapping("/{id}/mark-won")
    public OpportunityResponse markWon(
            @PathVariable String id,
            @RequestBody MarkWonRequest request) {
        return opportunityAppService.markWon(id, request.actualAmount());
    }

    @PostMapping("/{id}/mark-lost")
    public OpportunityResponse markLost(
            @PathVariable String id,
            @RequestBody MarkLostRequest request) {
        return opportunityAppService.markLost(id, request.reason());
    }

    @GetMapping("/customer/{customerId}")
    public List<OpportunityResponse> findByCustomerId(@PathVariable String customerId) {
        return opportunityAppService.findByCustomerId(customerId);
    }

    public record AdvanceStageRequest(String stage) {}
    public record MarkWonRequest(BigDecimal actualAmount) {}
    public record MarkLostRequest(String reason) {}
}
