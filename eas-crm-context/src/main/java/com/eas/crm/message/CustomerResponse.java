package com.eas.crm.message;

import com.eas.crm.domain.customer.CustomerLevel;
import com.eas.crm.domain.customer.CustomerSource;
import com.eas.crm.domain.customer.CustomerId;

import java.time.LocalDateTime;
import java.util.List;

public record CustomerResponse(
        String id,
        String name,
        String industry,
        CustomerLevel level,
        CustomerSource source,
        String contactName,
        String contactPhone,
        String address,
        String creatorId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        List<ContactInfo> contacts
) {
    public record ContactInfo(
            String id,
            String name,
            String phone,
            String email,
            String position,
            boolean isPrimary
    ) {}
}
