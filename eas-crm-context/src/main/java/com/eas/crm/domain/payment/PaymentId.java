package com.eas.crm.domain.payment;

import java.util.Objects;

public record PaymentId(String value) {
    public PaymentId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Payment ID cannot be null or blank");
        }
    }

    public static PaymentId of(String value) {
        return new PaymentId(value);
    }

    public static PaymentId generate() {
        return new PaymentId(java.util.UUID.randomUUID().toString());
    }
}
