package com.financesystem.finance_api.modules.tenant.transactions.domain.exception;

import com.financesystem.finance_api.common.exception.ResourceNotFoundException;

public class TransactionNotFoundException extends ResourceNotFoundException {

    public TransactionNotFoundException(String message) {
        super(message);
    }
}
