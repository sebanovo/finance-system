package com.financesystem.finance_api.modules.tenant.loans.domain.exception;

import com.financesystem.finance_api.common.exception.BusinessException;

public class InvalidLoanOperationException extends BusinessException {

    public InvalidLoanOperationException(String message) {
        super(message);
    }
}
