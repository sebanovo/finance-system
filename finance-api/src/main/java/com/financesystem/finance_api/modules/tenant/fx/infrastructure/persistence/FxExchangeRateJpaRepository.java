package com.financesystem.finance_api.modules.tenant.fx.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface FxExchangeRateJpaRepository extends JpaRepository<FxExchangeRateEntity, UUID> {

    Optional<FxExchangeRateEntity> findBySourceCurrencyAndTargetCurrency(String sourceCurrency, String targetCurrency);
}
