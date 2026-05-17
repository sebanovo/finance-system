package com.financesystem.finance_api.modules.tenant.accounting.application.dto;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record AccountingPeriodResponse(
        UUID id,
        String periodCode,
        String periodType,
        String status,
        LocalDate startDate,
        LocalDate endDate,
        Instant closedAt,
        String description,
        Instant createdAt,
        Instant updatedAt
) {
}
