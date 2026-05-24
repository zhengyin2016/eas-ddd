package com.eas.crm.message;

import com.eas.crm.domain.contract.ContractStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record ContractResponse(
        String id,
        String customerId,
        String opportunityId,
        String title,
        BigDecimal amount,
        ContractStatus status,
        LocalDate signDate,
        LocalDate startDate,
        LocalDate endDate,
        BigDecimal paidAmount,
        BigDecimal unpaidAmount,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        String approverId,
        String rejectReason,
        List<PaymentPlanInfo> paymentPlans
) {
    public record PaymentPlanInfo(
            String id,
            BigDecimal amount,
            LocalDate dueDate,
            String status,
            LocalDateTime paidAt
    ) {}
}
