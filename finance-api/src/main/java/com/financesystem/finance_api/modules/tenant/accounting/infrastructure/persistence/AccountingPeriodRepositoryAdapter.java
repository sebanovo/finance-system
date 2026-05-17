package com.financesystem.finance_api.modules.tenant.accounting.infrastructure.persistence;

import com.financesystem.finance_api.modules.tenant.accounting.domain.model.AccountingPeriod;
import com.financesystem.finance_api.modules.tenant.accounting.domain.model.AccountingPeriodStatus;
import com.financesystem.finance_api.modules.tenant.accounting.domain.model.AccountingPeriodType;
import com.financesystem.finance_api.modules.tenant.accounting.domain.repository.AccountingPeriodRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class AccountingPeriodRepositoryAdapter implements AccountingPeriodRepository {

    private final AccountingPeriodJpaRepository jpaRepository;

    public AccountingPeriodRepositoryAdapter(AccountingPeriodJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public AccountingPeriod save(AccountingPeriod accountingPeriod) {
        AccountingPeriodEntity entity = toEntity(accountingPeriod);
        return toDomain(jpaRepository.save(entity));
    }

    @Override
    public Optional<AccountingPeriod> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<AccountingPeriod> findByPeriodCode(String periodCode) {
        return jpaRepository.findByPeriodCode(periodCode).map(this::toDomain);
    }

    @Override
    public List<AccountingPeriod> findAll() {
        return jpaRepository.findAll().stream().map(this::toDomain).toList();
    }

    private AccountingPeriodEntity toEntity(AccountingPeriod period) {
        AccountingPeriodEntity entity = new AccountingPeriodEntity();
        entity.setId(period.id());
        entity.setPeriodCode(period.periodCode());
        entity.setPeriodType(period.periodType().name());
        entity.setStatus(period.status().name());
        entity.setStartDate(period.startDate());
        entity.setEndDate(period.endDate());
        entity.setClosedAt(period.closedAt());
        entity.setDescription(period.description());
        return entity;
    }

    private AccountingPeriod toDomain(AccountingPeriodEntity entity) {
        return new AccountingPeriod(
                entity.getId(),
                entity.getPeriodCode(),
                AccountingPeriodType.valueOf(entity.getPeriodType()),
                AccountingPeriodStatus.valueOf(entity.getStatus()),
                entity.getStartDate(),
                entity.getEndDate(),
                entity.getClosedAt(),
                entity.getDescription(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
