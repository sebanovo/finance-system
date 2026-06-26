package com.financesystem.finance_api.modules.tenant.loans.application.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record LoanResponse(
        UUID id,
        UUID userId,
        UUID accountId,
        BigDecimal principal,
        String currency,
        BigDecimal annualInterestRate,
        int termMonths,
        String interestMethod,
        String status,
        String purpose,
        String statusReason,
        BigDecimal totalDue,
        BigDecimal totalPaid,
        BigDecimal outstandingBalance,
        Instant disbursedAt,
        Instant closedAt,
        Instant createdAt,
        Instant updatedAt
) {
}
