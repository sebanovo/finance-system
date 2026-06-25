package com.financesystem.finance_api.modules.tenant.loans.domain.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record LoanPayment(
        UUID id,
        UUID loanId,
        BigDecimal amount,
        UUID transactionId,
        Instant paidAt,
        Instant createdAt
) {
}
