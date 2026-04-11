package com.financesystem.finance_api.modules.platform.superadmin.domain.exception;

import com.financesystem.finance_api.common.exception.ResourceNotFoundException;

public class PlatformSuperadminNotFoundException extends ResourceNotFoundException {

    public PlatformSuperadminNotFoundException(String message) {
        super(message);
    }
}