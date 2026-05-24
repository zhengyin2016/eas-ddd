package com.eas.crm.domain.contract;

import java.util.Objects;

public record ContractId(String value) {
    public ContractId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Contract ID cannot be null or blank");
        }
    }

    public static ContractId of(String value) {
        return new ContractId(value);
    }

    public static ContractId generate() {
        return new ContractId(java.util.UUID.randomUUID().toString());
    }
}
