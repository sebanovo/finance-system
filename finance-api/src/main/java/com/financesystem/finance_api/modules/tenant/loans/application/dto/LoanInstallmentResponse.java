package com.financesystem.finance_api.modules.tenant.loans.application.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record LoanInstallmentResponse(
        UUID id,
        int number,
        LocalDate dueDate,
        BigDecimal principalDue,
        BigDecimal interestDue,
        BigDecimal totalDue,
        BigDecimal paidAmount,
        String status,
        Instant paidAt
) {
}
