package com.financesystem.finance.modules.identity.users.domain.exception;

import com.financesystem.finance.common.exception.BusinessException;

public class TenantUserAlreadyExistsException extends BusinessException {

    public TenantUserAlreadyExistsException(String message) {
        super(message);
    }
}