package com.financesystem.finance.modules.identity.auth.domain.exception;

import com.financesystem.finance.common.exception.BusinessException;

public class InvalidPasswordResetTokenException extends BusinessException {

    public InvalidPasswordResetTokenException(String message) {
        super(message);
    }
}