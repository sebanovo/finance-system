package com.financesystem.finance_api.modules.tenant.loans.domain.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record LoanInstallment(
        UUID id,
        UUID loanId,
        int number,
        LocalDate dueDate,
        BigDecimal principalDue,
        BigDecimal interestDue,
        BigDecimal totalDue,
        BigDecimal paidAmount,
        LoanInstallmentStatus status,
        Instant paidAt,
        Instant createdAt,
        Instant updatedAt
) {
}
