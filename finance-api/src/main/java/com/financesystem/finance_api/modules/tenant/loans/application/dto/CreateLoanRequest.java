package com.financesystem.finance_api.modules.tenant.loans.application.dto;

import com.financesystem.finance_api.modules.tenant.loans.domain.model.InterestMethod;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateLoanRequest(
        @NotNull
        UUID userId,

        @NotNull
        UUID accountId,

        @NotNull
        @DecimalMin("0.01")
        @Digits(integer = 16, fraction = 2)
        BigDecimal principal,

        @NotNull
        @DecimalMin("0.00")
        @Digits(integer = 4, fraction = 2)
        BigDecimal annualInterestRate,

        @NotNull
        @Min(1)
        @Max(600)
        Integer termMonths,

        InterestMethod interestMethod,

        @Size(max = 255)
        String purpose
) {
}
