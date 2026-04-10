package com.financesystem.finance_api.modules.identity.users.domain.exception;

import com.financesystem.finance_api.common.exception.BusinessException;

public class TenantUserAlreadyExistsException extends BusinessException {

    public TenantUserAlreadyExistsException(String message) {
        super(message);
    }
}