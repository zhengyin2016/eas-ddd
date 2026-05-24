package com.eas.crm.southbound.adapter.repository;

import com.eas.crm.domain.customer.CustomerId;
import com.eas.crm.domain.opportunity.*;
import com.eas.crm.southbound.mapper.OpportunityMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class OpportunityRepositoryImpl implements OpportunityRepository {

    private final OpportunityMapper opportunityMapper;

    public OpportunityRepositoryImpl(OpportunityMapper opportunityMapper) {
        this.opportunityMapper = opportunityMapper;
    }

    @Override
    public Opportunity save(Opportunity opportunity) {
        boolean exists = opportunityMapper.existsById(opportunity.getId().value());

        if (exists) {
            opportunityMapper.update(opportunity);
        } else {
            opportunityMapper.insert(opportunity);
        }

        return opportunity;
    }

    @Override
    public Optional<Opportunity> findById(OpportunityId id) {
        return opportunityMapper.findById(id.value())
                .map(this::restoreOpportunity);
    }

    @Override
    public List<Opportunity> findByCustomerId(CustomerId customerId) {
        return opportunityMapper.findByCustomerId(customerId.value()).stream()
                .map(this::restoreOpportunity)
                .collect(Collectors.toList());
    }

    @Override
    public List<Opportunity> findByStage(OpportunityStage stage) {
        return opportunityMapper.findByStage(stage.name()).stream()
                .map(this::restoreOpportunity)
                .collect(Collectors.toList());
    }

    @Override
    public List<Opportunity> findByOwnerId(String ownerId) {
        return opportunityMapper.findByOwnerId(ownerId).stream()
                .map(this::restoreOpportunity)
                .collect(Collectors.toList());
    }

    @Override
    public List<Opportunity> findAll() {
        return opportunityMapper.findAll().stream()
                .map(this::restoreOpportunity)
                .collect(Collectors.toList());
    }

    private Opportunity restoreOpportunity(OpportunityMapper.OpportunityDO DO) {
        return Opportunity.restore(
                OpportunityId.of(DO.id()),
                CustomerId.of(DO.customerId()),
                DO.title(),
                Money.of(DO.estimatedAmount()),
                OpportunityStage.valueOf(DO.stage()),
                DO.probability(),
                DO.expectedCloseDate(),
                DO.ownerId(),
                DO.isWon(),
                DO.isLost(),
                DO.lostReason(),
                DO.actualAmount() != null ? Money.of(DO.actualAmount()) : null,
                DO.createdAt(),
                DO.updatedAt()
        );
    }
}
