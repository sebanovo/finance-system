package com.financesystem.finance_api.modules.tenant.fx.infrastructure.persistence;

import com.financesystem.finance_api.modules.tenant.accounts.domain.model.CurrencyCode;
import com.financesystem.finance_api.modules.tenant.fx.domain.model.FxExchangeRate;
import com.financesystem.finance_api.modules.tenant.fx.domain.repository.FxExchangeRateRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class FxExchangeRateRepositoryAdapter implements FxExchangeRateRepository {

    private final FxExchangeRateJpaRepository jpaRepository;

    public FxExchangeRateRepositoryAdapter(FxExchangeRateJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public FxExchangeRate save(FxExchangeRate exchangeRate) {
        return toDomain(jpaRepository.save(toEntity(exchangeRate)));
    }

    @Override
    public Optional<FxExchangeRate> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<FxExchangeRate> findBySourceCurrencyAndTargetCurrency(CurrencyCode sourceCurrency, CurrencyCode targetCurrency) {
        return jpaRepository.findBySourceCurrencyAndTargetCurrency(sourceCurrency.name(), targetCurrency.name()).map(this::toDomain);
    }

    @Override
    public List<FxExchangeRate> findAll() {
        return jpaRepository.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

    private FxExchangeRateEntity toEntity(FxExchangeRate rate) {
        FxExchangeRateEntity entity = new FxExchangeRateEntity();
        entity.setId(rate.id());
        entity.setSourceCurrency(rate.sourceCurrency().name());
        entity.setTargetCurrency(rate.targetCurrency().name());
        entity.setRate(rate.rate());
        entity.setActive(rate.active());
        entity.setDescription(rate.description());
        entity.setCreatedAt(rate.createdAt());
        entity.setUpdatedAt(rate.updatedAt());
        return entity;
    }

    private FxExchangeRate toDomain(FxExchangeRateEntity entity) {
        return new FxExchangeRate(
                entity.getId(),
                CurrencyCode.valueOf(entity.getSourceCurrency()),
                CurrencyCode.valueOf(entity.getTargetCurrency()),
                entity.getRate(),
                entity.isActive(),
                entity.getDescription(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
