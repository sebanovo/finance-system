package com.financesystem.finance.modules.identity.users.domain.exception;

import com.financesystem.finance.common.exception.ResourceNotFoundException;

public class TenantUserNotFoundException extends ResourceNotFoundException {

    public TenantUserNotFoundException(String message) {
        super(message);
    }
}