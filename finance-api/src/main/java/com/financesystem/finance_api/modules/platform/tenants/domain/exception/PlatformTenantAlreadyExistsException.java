package com.financesystem.finance_api.modules.platform.tenants.domain.exception;

import com.financesystem.finance_api.common.exception.BusinessException;

public class PlatformTenantAlreadyExistsException extends BusinessException {

    public PlatformTenantAlreadyExistsException(String message) {
        super(message);
    }
}