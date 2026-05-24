package com.eas.crm.northbound.appservice;

import com.eas.crm.domain.customer.CustomerId;
import com.eas.crm.domain.customer.CustomerRepository;
import com.eas.crm.domain.opportunity.*;
import com.eas.crm.message.CreateOpportunityRequest;
import com.eas.crm.message.OpportunityResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OpportunityAppService {

    private final OpportunityRepository opportunityRepository;
    private final CustomerRepository customerRepository;

    public OpportunityAppService(OpportunityRepository opportunityRepository,
                                 CustomerRepository customerRepository) {
        this.opportunityRepository = opportunityRepository;
        this.customerRepository = customerRepository;
    }

    @Transactional
    public OpportunityResponse createOpportunity(CreateOpportunityRequest request) {
        // 验证客户存在
        if (customerRepository.findById(CustomerId.of(request.customerId())).isEmpty()) {
            throw new IllegalArgumentException("Customer not found: " + request.customerId());
        }

        Opportunity opportunity = Opportunity.create(
                CustomerId.of(request.customerId()),
                request.title(),
                Money.of(request.estimatedAmount()),
                request.expectedCloseDate(),
                request.ownerId()
        );

        Opportunity saved = opportunityRepository.save(opportunity);
        return toResponse(saved);
    }

    @Transactional
    public OpportunityResponse advanceStage(String opportunityId, String newStage) {
        Opportunity opportunity = opportunityRepository.findById(OpportunityId.of(opportunityId))
                .orElseThrow(() -> new IllegalArgumentException("Opportunity not found: " + opportunityId));

        opportunity.advanceStage(OpportunityStage.valueOf(newStage));
        Opportunity updated = opportunityRepository.save(opportunity);

        // 如果商机赢单，更新关联的客户等级评估（简化处理，实际应该通过领域事件）
        if (updated.isWon()) {
            evaluateCustomerLevel(updated.getCustomerId());
        }

        return toResponse(updated);
    }

    @Transactional
    public OpportunityResponse markWon(String opportunityId, BigDecimal actualAmount) {
        Opportunity opportunity = opportunityRepository.findById(OpportunityId.of(opportunityId))
                .orElseThrow(() -> new IllegalArgumentException("Opportunity not found: " + opportunityId));

        opportunity.markWon(Money.of(actualAmount));
        Opportunity updated = opportunityRepository.save(opportunity);

        // 赢单后评估客户等级
        evaluateCustomerLevel(updated.getCustomerId());

        return toResponse(updated);
    }

    @Transactional
    public OpportunityResponse markLost(String opportunityId, String reason) {
        Opportunity opportunity = opportunityRepository.findById(OpportunityId.of(opportunityId))
                .orElseThrow(() -> new IllegalArgumentException("Opportunity not found: " + opportunityId));

        opportunity.markLost(reason);
        Opportunity updated = opportunityRepository.save(opportunity);
        return toResponse(updated);
    }

    @Transactional(readOnly = true)
    public List<OpportunityResponse> findByCustomerId(String customerId) {
        return opportunityRepository.findByCustomerId(CustomerId.of(customerId)).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public OpportunityResponse getOpportunity(String opportunityId) {
        Opportunity opportunity = opportunityRepository.findById(OpportunityId.of(opportunityId))
                .orElseThrow(() -> new IllegalArgumentException("Opportunity not found: " + opportunityId));
        return toResponse(opportunity);
    }

    private void evaluateCustomerLevel(CustomerId customerId) {
        // 简化的客户等级评估逻辑
        // 实际应该统计客户累计交易额和频率
        customerRepository.findById(customerId).ifPresent(customer -> {
            // 这里只是示例，实际应该有更复杂的评估逻辑
            if (customer.getLevel() == CustomerLevel.D) {
                customer.upgradeLevel();
                customerRepository.save(customer);
            }
        });
    }

    private OpportunityResponse toResponse(Opportunity opportunity) {
        return new OpportunityResponse(
                opportunity.getId().value(),
                opportunity.getCustomerId().value(),
                opportunity.getTitle(),
                new BigDecimal(opportunity.getEstimatedAmount().amount().toString()),
                opportunity.getStage(),
                opportunity.getProbability(),
                opportunity.getExpectedCloseDate(),
                opportunity.getOwnerId(),
                opportunity.isWon(),
                opportunity.isLost(),
                opportunity.getLostReason(),
                opportunity.getActualAmount() != null
                        ? new BigDecimal(opportunity.getActualAmount().amount().toString())
                        : null,
                opportunity.getCreatedAt(),
                opportunity.getUpdatedAt()
        );
    }
}
