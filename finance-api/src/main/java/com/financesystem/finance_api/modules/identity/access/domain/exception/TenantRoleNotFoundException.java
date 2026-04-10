package com.financesystem.finance_api.modules.identity.access.domain.exception;

import com.financesystem.finance_api.common.exception.ResourceNotFoundException;

public class TenantRoleNotFoundException extends ResourceNotFoundException {

    public TenantRoleNotFoundException(String message) {
        super(message);
    }
}