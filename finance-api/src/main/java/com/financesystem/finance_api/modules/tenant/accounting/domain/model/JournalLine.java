package com.financesystem.finance_api.modules.tenant.accounting.domain.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record JournalLine(
        UUID id,
        UUID journalEntryId,
        int lineNo,
        String accountCode,
        String accountName,
        JournalLineType lineType,
        BigDecimal amount,
        String currency,
        String description,
        Instant createdAt
) {
}
