package com.financesystem.finance.modules.platform.superadmin.domain.exception;

import com.financesystem.finance.common.exception.ResourceNotFoundException;

public class PlatformSuperadminNotFoundException extends ResourceNotFoundException {

    public PlatformSuperadminNotFoundException(String message) {
        super(message);
    }
}