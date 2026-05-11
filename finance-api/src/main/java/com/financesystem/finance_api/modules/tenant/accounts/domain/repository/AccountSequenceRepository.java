package com.financesystem.finance_api.modules.tenant.accounts.domain.repository;

import com.financesystem.finance_api.modules.tenant.accounts.domain.model.AccountType;
import com.financesystem.finance_api.modules.tenant.accounts.domain.model.CurrencyCode;

public interface AccountSequenceRepository {

    long nextValue(AccountType accountType, CurrencyCode currency);
}
