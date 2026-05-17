package com.financesystem.finance_api.modules.tenant.accounting.domain.model;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record AccountingPeriod(
        UUID id,
        String periodCode,
        AccountingPeriodType periodType,
        AccountingPeriodStatus status,
        LocalDate startDate,
        LocalDate endDate,
        Instant closedAt,
        String description,
        Instant createdAt,
        Instant updatedAt
) {
}
