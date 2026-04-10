package com.financesystem.finance_api.modules.identity.access.domain.exception;

import com.financesystem.finance_api.common.exception.BusinessException;

public class TenantRoleAlreadyExistsException extends BusinessException {

    public TenantRoleAlreadyExistsException(String message) {
        super(message);
    }
}