package com.financesystem.finance_api.modules.identity.users.domain.exception;

import com.financesystem.finance_api.common.exception.ResourceNotFoundException;

public class TenantUserNotFoundException extends ResourceNotFoundException {

    public TenantUserNotFoundException(String message) {
        super(message);
    }
}