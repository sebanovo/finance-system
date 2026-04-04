package com.financesystem.finance.modules.identity.access.domain.exception;

import com.financesystem.finance.common.exception.BusinessException;

public class TenantRoleAlreadyExistsException extends BusinessException {

    public TenantRoleAlreadyExistsException(String message) {
        super(message);
    }
}