package com.financesystem.finance_api.modules.platform.tenants.domain.exception;

import com.financesystem.finance_api.common.exception.ResourceNotFoundException;

public class PlatformTenantNotFoundException extends ResourceNotFoundException {

    public PlatformTenantNotFoundException(String message) {
        super(message);
    }
}