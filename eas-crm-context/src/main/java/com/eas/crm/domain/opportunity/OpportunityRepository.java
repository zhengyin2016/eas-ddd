package com.eas.crm.domain.opportunity;

import com.eas.crm.domain.customer.CustomerId;

import java.util.List;
import java.util.Optional;

public interface OpportunityRepository {
    Opportunity save(Opportunity opportunity);
    Optional<Opportunity> findById(OpportunityId id);
    List<Opportunity> findByCustomerId(CustomerId customerId);
    List<Opportunity> findByStage(OpportunityStage stage);
    List<Opportunity> findByOwnerId(String ownerId);
    List<Opportunity> findAll();
}
