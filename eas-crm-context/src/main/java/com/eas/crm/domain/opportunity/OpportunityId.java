package com.eas.crm.domain.opportunity;

import java.util.Objects;

public record OpportunityId(String value) {
    public OpportunityId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Opportunity ID cannot be null or blank");
        }
    }

    public static OpportunityId of(String value) {
        return new OpportunityId(value);
    }

    public static OpportunityId generate() {
        return new OpportunityId(java.util.UUID.randomUUID().toString());
    }
}
