package com.financesystem.finance_api.modules.tenant.accounts.domain.exception;

import com.financesystem.finance_api.common.exception.BusinessException;

public class AccountAlreadyExistsException extends BusinessException {

    public AccountAlreadyExistsException(String message) {
        super(message);
    }
}
