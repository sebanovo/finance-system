package com.financesystem.finance_api.modules.tenant.accounts.domain.model;

import java.time.Instant;
import java.util.UUID;

public record AccountSequence(
        UUID id,
        AccountType accountType,
        CurrencyCode currency,
        Long currentValue,
        Instant createdAt,
        Instant updatedAt
) {
}
