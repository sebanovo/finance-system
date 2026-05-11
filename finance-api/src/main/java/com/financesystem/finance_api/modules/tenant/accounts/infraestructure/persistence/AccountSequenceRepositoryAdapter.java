package com.financesystem.finance_api.modules.tenant.accounts.infraestructure.persistence;

import com.financesystem.finance_api.modules.tenant.accounts.domain.model.AccountType;
import com.financesystem.finance_api.modules.tenant.accounts.domain.model.CurrencyCode;
import com.financesystem.finance_api.modules.tenant.accounts.domain.repository.AccountSequenceRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class AccountSequenceRepositoryAdapter implements AccountSequenceRepository {

    private final AccountSequenceJpaRepository jpaRepository;

    public AccountSequenceRepositoryAdapter(AccountSequenceJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    @Transactional
    public long nextValue(AccountType accountType, CurrencyCode currency) {
        jpaRepository.ensureSequenceExists(accountType.name(), currency.name());
        return jpaRepository.incrementAndReturn(accountType.name(), currency.name());
    }
}
