package com.financesystem.finance_api.modules.tenant.accounting.application.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record JournalEntryResponse(
        UUID id,
        String entryNumber,
        UUID sourceTransactionId,
        String entryType,
        String status,
        String description,
        String reference,
        BigDecimal totalDebits,
        BigDecimal totalCredits,
        boolean balanced,
        Instant postedAt,
        Instant createdAt,
        Instant updatedAt,
        List<JournalLineResponse> lines
) {
}
