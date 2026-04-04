package com.financesystem.finance.modules.identity.access.domain.exception;

import com.financesystem.finance.common.exception.ResourceNotFoundException;

public class TenantRoleNotFoundException extends ResourceNotFoundException {

    public TenantRoleNotFoundException(String message) {
        super(message);
    }
}