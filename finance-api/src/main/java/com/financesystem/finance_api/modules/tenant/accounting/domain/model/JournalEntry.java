package com.financesystem.finance_api.modules.tenant.accounting.domain.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record JournalEntry(
        UUID id,
        String entryNumber,
        UUID sourceTransactionId,
        UUID periodId,
        JournalEntryType entryType,
        JournalEntryStatus status,
        String description,
        String reference,
        BigDecimal totalDebits,
        BigDecimal totalCredits,
        Instant postedAt,
        Instant createdAt,
        Instant updatedAt,
        List<JournalLine> lines
) {
}
