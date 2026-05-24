package com.eas.crm.domain.customer;

import java.util.Objects;

public record ContactId(String value) {
    public ContactId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Contact ID cannot be null or blank");
        }
    }

    public static ContactId of(String value) {
        return new ContactId(value);
    }

    public static ContactId generate() {
        return new ContactId(java.util.UUID.randomUUID().toString());
    }
}
