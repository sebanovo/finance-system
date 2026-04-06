package com.financesystem.finance.modules.platform.subscriptions.domain.exception;

import com.financesystem.finance.common.exception.BusinessException;

public class TenantSubscriptionAccessDeniedException extends BusinessException {

    public TenantSubscriptionAccessDeniedException(String message) {
        super(message);
    }
}
