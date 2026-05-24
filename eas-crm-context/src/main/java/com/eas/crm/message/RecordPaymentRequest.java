package com.eas.crm.message;

import com.eas.crm.domain.payment.PaymentMethod;

import java.math.BigDecimal;
import java.time.LocalDate;

public record RecordPaymentRequest(
        String contractId,
        BigDecimal amount,
        LocalDate paymentDate,
        PaymentMethod paymentMethod,
        String remark
) {
    public RecordPaymentRequest {
        if (contractId == null || contractId.isBlank()) {
            throw new IllegalArgumentException("Contract ID cannot be null or blank");
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Payment amount must be greater than zero");
        }
    }
}
