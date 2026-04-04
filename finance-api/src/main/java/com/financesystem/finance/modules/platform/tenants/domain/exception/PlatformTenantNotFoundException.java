package com.financesystem.finance.modules.platform.tenants.domain.exception;

import com.financesystem.finance.common.exception.ResourceNotFoundException;

public class PlatformTenantNotFoundException extends ResourceNotFoundException {

    public PlatformTenantNotFoundException(String message) {
        super(message);
    }
}