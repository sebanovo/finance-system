package com.financesystem.finance.modules.identity.access.domain.exception;

import com.financesystem.finance.common.exception.BusinessException;

public class InvalidSystemPermissionException extends BusinessException {

    public InvalidSystemPermissionException(String message) {
        super(message);
    }
}