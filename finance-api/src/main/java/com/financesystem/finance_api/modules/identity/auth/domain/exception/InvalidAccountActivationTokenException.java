package com.financesystem.finance_api.modules.identity.auth.domain.exception;

import com.financesystem.finance_api.common.exception.BusinessException;

public class InvalidAccountActivationTokenException extends BusinessException {

    public InvalidAccountActivationTokenException(String message) {
        super(message);
    }
}
