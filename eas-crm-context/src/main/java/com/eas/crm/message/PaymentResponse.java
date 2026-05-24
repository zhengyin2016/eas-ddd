package com.eas.crm.message;

import com.eas.crm.domain.payment.PaymentMethod;
import com.eas.crm.domain.payment.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record PaymentResponse(
        String id,
        String contractId,
        BigDecimal amount,
        LocalDate paymentDate,
        PaymentMethod paymentMethod,
        PaymentStatus status,
        String remark,
        LocalDateTime createdAt,
        LocalDateTime confirmedAt
) {}
