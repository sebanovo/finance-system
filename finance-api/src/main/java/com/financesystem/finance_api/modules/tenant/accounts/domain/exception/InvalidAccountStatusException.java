package com.financesystem.finance_api.modules.tenant.accounts.domain.exception;

import com.financesystem.finance_api.common.exception.BusinessException;

public class InvalidAccountStatusException extends BusinessException {

    public InvalidAccountStatusException(String message) {
        super(message);
    }
}