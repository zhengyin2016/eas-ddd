package com.eas.crm.southbound.port.client.pm;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateProjectCommand(
        String name,
        String customerId,
        String contractId,
        BigDecimal budget,
        LocalDate startDate,
        LocalDate endDate
) {}
