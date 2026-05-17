package com.financesystem.finance_api.modules.tenant.fx.application.dto;

import com.financesystem.finance_api.modules.tenant.accounts.domain.model.CurrencyCode;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record FxExchangeRateResponse(
        UUID id,
        CurrencyCode sourceCurrency,
        CurrencyCode targetCurrency,
        BigDecimal rate,
        boolean active,
        String description,
        Instant createdAt,
        Instant updatedAt
) {
}
