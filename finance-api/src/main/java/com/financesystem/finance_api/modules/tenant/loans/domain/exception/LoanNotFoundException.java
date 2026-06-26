package com.financesystem.finance_api.modules.tenant.loans.domain.exception;

import com.financesystem.finance_api.common.exception.ResourceNotFoundException;

public class LoanNotFoundException extends ResourceNotFoundException {

    public LoanNotFoundException(String message) {
        super(message);
    }
}
