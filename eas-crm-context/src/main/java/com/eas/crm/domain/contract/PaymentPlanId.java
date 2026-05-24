package com.eas.crm.domain.contract;

import java.util.Objects;

public record PaymentPlanId(String value) {
    public PaymentPlanId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Payment plan ID cannot be null or blank");
        }
    }

    public static PaymentPlanId of(String value) {
        return new PaymentPlanId(value);
    }

    public static PaymentPlanId generate() {
        return new PaymentPlanId(java.util.UUID.randomUUID().toString());
    }
}
