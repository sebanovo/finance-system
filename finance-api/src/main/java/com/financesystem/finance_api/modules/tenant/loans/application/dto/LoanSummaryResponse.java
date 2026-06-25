package com.financesystem.finance_api.modules.tenant.loans.application.dto;

import java.math.BigDecimal;

public record LoanSummaryResponse(
        long totalLoans,
        long requested,
        long approved,
        long disbursed,
        long paidOff,
        long rejected,
        BigDecimal totalDisbursedPrincipal,
        BigDecimal totalOutstanding
) {
}
