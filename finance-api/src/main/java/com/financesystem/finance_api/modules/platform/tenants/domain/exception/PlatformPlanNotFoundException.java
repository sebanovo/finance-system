package com.financesystem.finance_api.modules.platform.tenants.domain.exception;

import com.financesystem.finance_api.common.exception.ResourceNotFoundException;

public class PlatformPlanNotFoundException extends ResourceNotFoundException {

    public PlatformPlanNotFoundException(String message) {
        super(message);
    }
}