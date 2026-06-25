package com.financesystem.finance_api.modules.tenant.loans.application.dto;

public record ProcessOverdueLoansResponse(
        int overdueInstallments,
        int affectedLoans
) {
}
