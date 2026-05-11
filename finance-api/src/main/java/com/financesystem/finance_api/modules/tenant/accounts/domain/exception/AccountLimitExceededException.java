package com.financesystem.finance_api.modules.tenant.accounts.domain.exception;

import com.financesystem.finance_api.common.exception.BusinessException;

public class AccountLimitExceededException extends BusinessException {

    public AccountLimitExceededException(String message) {
        super(message);
    }
}