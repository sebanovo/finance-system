package com.financesystem.finance_api.modules.platform.servicepayments.application.dto;

import com.financesystem.finance_api.modules.tenant.accounts.domain.model.CurrencyCode;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record CreateServiceBillRequest(
        @NotNull
        UUID providerId,

        @NotBlank
        @Size(max = 100)
        String serviceCustomerCode,

        @NotBlank
        @Pattern(regexp = "^\\d{4}-\\d{2}$")
        String billingPeriod,

        @NotNull
        @DecimalMin("0.01")
        BigDecimal amount,

        @NotNull
        CurrencyCode currency,

        @NotNull
        LocalDate dueDate
) {
}
