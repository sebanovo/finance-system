package com.financesystem.finance_api.modules.tenant.accounting.application.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record JournalLineResponse(
        UUID id,
        int lineNo,
        String accountCode,
        String accountName,
        String lineType,
        BigDecimal amount,
        String currency,
        String description,
        Instant createdAt
) {
}
