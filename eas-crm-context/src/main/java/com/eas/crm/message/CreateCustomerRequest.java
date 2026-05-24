package com.eas.crm.message;

import com.eas.crm.domain.customer.CustomerSource;

public record CreateCustomerRequest(
        String name,
        String industry,
        CustomerSource source,
        String contactName,
        String contactPhone,
        String contactEmail,
        String address,
        String creatorId
) {
    public CreateCustomerRequest {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Customer name cannot be null or blank");
        }
        if (contactName == null || contactName.isBlank()) {
            throw new IllegalArgumentException("Contact name cannot be null or blank");
        }
        if (creatorId == null || creatorId.isBlank()) {
            throw new IllegalArgumentException("Creator ID cannot be null or blank");
        }
    }
}
