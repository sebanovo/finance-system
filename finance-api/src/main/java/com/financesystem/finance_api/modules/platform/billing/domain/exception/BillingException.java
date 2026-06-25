package com.financesystem.finance_api.modules.platform.billing.domain.exception;

import com.financesystem.finance_api.common.exception.BusinessException;

public class BillingException extends BusinessException {

    public BillingException(String message) {
        super(message);
    }

    public BillingException(String message, Throwable cause) {
        super(message, cause);
    }
}
