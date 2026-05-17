package com.financesystem.finance_api.modules.tenant.accounting.domain.exception;

import com.financesystem.finance_api.common.exception.ResourceNotFoundException;

public class AccountingPeriodNotFoundException extends ResourceNotFoundException {

    public AccountingPeriodNotFoundException(String message) {
        super(message);
    }
}
