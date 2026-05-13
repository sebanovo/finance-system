package com.financesystem.finance_api.modules.tenant.accounts.domain.exception;

import com.financesystem.finance_api.common.exception.ResourceNotFoundException;

public class AccountNotFoundException extends ResourceNotFoundException {

    public AccountNotFoundException(String message) {
        super(message);
    }
}
