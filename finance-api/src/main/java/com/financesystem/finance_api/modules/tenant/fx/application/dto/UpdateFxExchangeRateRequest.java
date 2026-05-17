package com.financesystem.finance_api.modules.tenant.fx.application.dto;

import com.financesystem.finance_api.modules.tenant.accounts.domain.model.CurrencyCode;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record UpdateFxExchangeRateRequest(
        @NotNull CurrencyCode sourceCurrency,
        @NotNull CurrencyCode targetCurrency,
        @NotNull @DecimalMin(value = "0.00000001") BigDecimal rate,
        @NotNull boolean active,
        String description
) {
}
