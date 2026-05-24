package com.eas.crm.message;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateContractRequest(
        String customerId,
        String opportunityId,
        String title,
        BigDecimal amount,
        LocalDate signDate,
        LocalDate startDate,
        LocalDate endDate
) {
    public CreateContractRequest {
        if (customerId == null || customerId.isBlank()) {
            throw new IllegalArgumentException("Customer ID cannot be null or blank");
        }
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Contract title cannot be null or blank");
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Contract amount must be greater than zero");
        }
        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date must be before end date");
        }
    }
}
