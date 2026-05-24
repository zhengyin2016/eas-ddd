package com.eas.crm.message;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateOpportunityRequest(
        String customerId,
        String title,
        BigDecimal estimatedAmount,
        LocalDate expectedCloseDate,
        String ownerId
) {
    public CreateOpportunityRequest {
        if (customerId == null || customerId.isBlank()) {
            throw new IllegalArgumentException("Customer ID cannot be null or blank");
        }
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Opportunity title cannot be null or blank");
        }
        if (estimatedAmount == null || estimatedAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Estimated amount must be greater than zero");
        }
        if (ownerId == null || ownerId.isBlank()) {
            throw new IllegalArgumentException("Owner ID cannot be null or blank");
        }
    }
}
