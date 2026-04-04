package com.financesystem.finance.modules.platform.tenants.domain.exception;

import com.financesystem.finance.common.exception.BusinessException;

public class PlatformTenantAlreadyExistsException extends BusinessException {

    public PlatformTenantAlreadyExistsException(String message) {
        super(message);
    }
}