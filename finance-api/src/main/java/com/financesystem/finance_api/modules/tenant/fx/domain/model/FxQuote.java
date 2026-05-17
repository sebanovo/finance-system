package com.financesystem.finance_api.modules.tenant.fx.domain.model;

import com.financesystem.finance_api.modules.tenant.accounts.domain.model.CurrencyCode;

import java.math.BigDecimal;

public record FxQuote(
        boolean applied,
        FxOperationCode operationCode,
        CurrencyCode sourceCurrency,
        CurrencyCode targetCurrency,
        BigDecimal sourceAmount,
        BigDecimal targetAmountGross,
        BigDecimal targetAmountNet,
        BigDecimal exchangeRate,
        BigDecimal effectiveExchangeRate,
        BigDecimal feeAmount,
        CurrencyCode feeCurrency,
        FeeType feeType,
        FeeCalculationMode calculationMode,
        boolean feeIncludedInRate
) {
}
