package com.financesystem.finance_api.modules.identity.auth.domain.exception;

import com.financesystem.finance_api.common.exception.BusinessException;

public class InvalidPasswordResetTokenException extends BusinessException {

    public InvalidPasswordResetTokenException(String message) {
        super(message);
    }
}