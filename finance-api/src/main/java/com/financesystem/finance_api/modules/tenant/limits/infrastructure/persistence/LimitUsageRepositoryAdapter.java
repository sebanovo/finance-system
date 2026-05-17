package com.financesystem.finance_api.modules.tenant.limits.infrastructure.persistence;

import com.financesystem.finance_api.modules.tenant.limits.domain.model.LimitUsage;
import com.financesystem.finance_api.modules.tenant.limits.domain.repository.LimitUsageRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class LimitUsageRepositoryAdapter implements LimitUsageRepository {

    private final LimitUsageJpaRepository jpaRepository;

    public LimitUsageRepositoryAdapter(LimitUsageJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public LimitUsage save(LimitUsage limitUsage) {
        LimitUsageEntity entity = toEntity(limitUsage);
        LimitUsageEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<LimitUsage> findByRuleIdAndScopeKeyAndPeriodKey(UUID limitRuleId, String scopeKey, String periodKey) {
        return jpaRepository.findByLimitRuleIdAndScopeKeyAndPeriodKey(limitRuleId, scopeKey, periodKey)
                .map(this::toDomain);
    }

    private LimitUsageEntity toEntity(LimitUsage limitUsage) {
        LimitUsageEntity entity = new LimitUsageEntity();
        entity.setId(limitUsage.id());
        entity.setLimitRuleId(limitUsage.limitRuleId());
        entity.setScopeKey(limitUsage.scopeKey());
        entity.setPeriodKey(limitUsage.periodKey());
        entity.setTransactionCount(limitUsage.transactionCount());
        entity.setTotalAmount(limitUsage.totalAmount());
        entity.setLastEvaluatedAt(limitUsage.lastEvaluatedAt());
        return entity;
    }

    private LimitUsage toDomain(LimitUsageEntity entity) {
        return new LimitUsage(
                entity.getId(),
                entity.getLimitRuleId(),
                entity.getScopeKey(),
                entity.getPeriodKey(),
                entity.getTransactionCount(),
                entity.getTotalAmount(),
                entity.getLastEvaluatedAt(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
