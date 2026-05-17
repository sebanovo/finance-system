package com.financesystem.finance_api.modules.tenant.fx.domain.repository;

import com.financesystem.finance_api.modules.tenant.accounts.domain.model.CurrencyCode;
import com.financesystem.finance_api.modules.tenant.fx.domain.model.FxExchangeRate;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FxExchangeRateRepository {

    FxExchangeRate save(FxExchangeRate exchangeRate);

    Optional<FxExchangeRate> findById(UUID id);

    Optional<FxExchangeRate> findBySourceCurrencyAndTargetCurrency(CurrencyCode sourceCurrency, CurrencyCode targetCurrency);

    List<FxExchangeRate> findAll();

    void deleteById(UUID id);
}
