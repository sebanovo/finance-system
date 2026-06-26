package com.financesystem.finance_api.modules.tenant.loans.application.dto;

import java.util.List;

public record LoanDetailResponse(
        LoanResponse loan,
        List<LoanInstallmentResponse> installments
) {
}
