package com.financesystem.finance.modules.platform.tenants.domain.exception;

import com.financesystem.finance.common.exception.ResourceNotFoundException;

public class PlatformPlanNotFoundException extends ResourceNotFoundException {

    public PlatformPlanNotFoundException(String message) {
        super(message);
    }
}