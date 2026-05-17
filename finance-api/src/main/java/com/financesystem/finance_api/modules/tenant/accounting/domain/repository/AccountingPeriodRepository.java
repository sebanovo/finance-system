package com.financesystem.finance_api.modules.tenant.accounting.domain.repository;

import com.financesystem.finance_api.modules.tenant.accounting.domain.model.AccountingPeriod;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AccountingPeriodRepository {

    AccountingPeriod save(AccountingPeriod accountingPeriod);

    Optional<AccountingPeriod> findById(UUID id);

    Optional<AccountingPeriod> findByPeriodCode(String periodCode);

    List<AccountingPeriod> findAll();
}
