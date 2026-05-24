package com.eas.crm.message;

import com.eas.crm.domain.opportunity.OpportunityStage;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record OpportunityResponse(
        String id,
        String customerId,
        String title,
        BigDecimal estimatedAmount,
        OpportunityStage stage,
        int probability,
        LocalDate expectedCloseDate,
        String ownerId,
        boolean isWon,
        boolean isLost,
        String lostReason,
        BigDecimal actualAmount,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
