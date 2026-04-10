package com.financesystem.finance_api.modules.identity.access.domain.exception;

import com.financesystem.finance_api.common.exception.BusinessException;

public class InvalidSystemPermissionException extends BusinessException {

    public InvalidSystemPermissionException(String message) {
        super(message);
    }
}