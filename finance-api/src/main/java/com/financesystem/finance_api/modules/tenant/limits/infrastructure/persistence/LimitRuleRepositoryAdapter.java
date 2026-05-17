package com.financesystem.finance_api.modules.tenant.limits.infrastructure.persistence;

import com.financesystem.finance_api.modules.tenant.accounts.domain.model.AccountType;
import com.financesystem.finance_api.modules.tenant.accounts.domain.model.CurrencyCode;
import com.financesystem.finance_api.modules.tenant.limits.domain.model.*;
import com.financesystem.finance_api.modules.tenant.limits.domain.repository.LimitRuleRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Sort;
import com.financesystem.finance_api.modules.tenant.transactions.domain.model.TransactionType;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class LimitRuleRepositoryAdapter implements LimitRuleRepository {

    private final LimitRuleJpaRepository jpaRepository;

    public LimitRuleRepositoryAdapter(LimitRuleJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public LimitRule save(LimitRule limitRule) {
        LimitRuleEntity entity = toEntity(limitRule);
        LimitRuleEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<LimitRule> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<LimitRule> findByCode(String code) {
        return jpaRepository.findByCode(code).map(this::toDomain);
    }

    @Override
    public List<LimitRule> findAll() {
        return jpaRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"))
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public List<LimitRule> findAllActive() {
        return jpaRepository.findAllByActiveTrueOrderByCreatedAtDesc()
                .stream()
                .map(this::toDomain)
                .toList();
    }

    private LimitRuleEntity toEntity(LimitRule limitRule) {
        LimitRuleEntity entity = new LimitRuleEntity();
        entity.setId(limitRule.id());
        entity.setCode(limitRule.code());
        entity.setName(limitRule.name());
        entity.setDescription(limitRule.description());
        entity.setLimitType(limitRule.limitType().name());
        entity.setScopeType(limitRule.scopeType().name());
        entity.setPeriod(limitRule.period().name());
        entity.setTransactionType(limitRule.transactionType() != null ? limitRule.transactionType().name() : null);
        entity.setAccountType(limitRule.accountType() != null ? limitRule.accountType().name() : null);
        entity.setCurrency(limitRule.currency() != null ? limitRule.currency().name() : null);
        entity.setMinAmount(limitRule.minAmount());
        entity.setMaxAmount(limitRule.maxAmount());
        entity.setMaxCount(limitRule.maxCount());
        entity.setActive(limitRule.active());
        entity.setRequireReviewExceed(limitRule.requireReviewExceed());
        entity.setCreatedByUserId(limitRule.createdByUserId());
        entity.setUpdatedByUserId(limitRule.updatedByUserId());
        return entity;
    }

    private LimitRule toDomain(LimitRuleEntity entity) {
        return new LimitRule(
                entity.getId(),
                entity.getCode(),
                entity.getName(),
                entity.getDescription(),
                LimitRuleType.valueOf(entity.getLimitType()),
                LimitScopeType.valueOf(entity.getScopeType()),
                LimitPeriod.valueOf(entity.getPeriod()),
                entity.getTransactionType() != null ? TransactionType.valueOf(entity.getTransactionType()) : null,
                entity.getAccountType() != null ? AccountType.valueOf(entity.getAccountType()) : null,
                entity.getCurrency() != null ? CurrencyCode.valueOf(entity.getCurrency()) : null,
                entity.getMinAmount(),
                entity.getMaxAmount(),
                entity.getMaxCount(),
                entity.isActive(),
                entity.isRequireReviewExceed(),
                entity.getCreatedByUserId(),
                entity.getUpdatedByUserId(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
