package com.financesystem.finance_api.modules.tenant.accounting.application.dto;

import com.financesystem.finance_api.modules.tenant.accounting.domain.model.AccountingPeriodType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record CreateAccountingPeriodRequest(
        @NotBlank
        @Size(max = 80)
        String periodCode,

        @NotNull
        AccountingPeriodType periodType,

        @NotNull
        LocalDate startDate,

        @NotNull
        LocalDate endDate,

        @Size(max = 255)
        String description
) {
}
