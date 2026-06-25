package com.financesystem.finance_api.modules.tenant.loans.domain.model;

import com.financesystem.finance_api.modules.tenant.accounts.domain.model.CurrencyCode;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record Loan(
        UUID id,
        UUID userId,
        UUID accountId,
        BigDecimal principal,
        CurrencyCode currency,
        BigDecimal annualInterestRate,
        int termMonths,
        InterestMethod interestMethod,
        LoanStatus status,
        String purpose,
        String statusReason,
        Instant disbursedAt,
        Instant closedAt,
        Instant createdAt,
        Instant updatedAt
) {
}
