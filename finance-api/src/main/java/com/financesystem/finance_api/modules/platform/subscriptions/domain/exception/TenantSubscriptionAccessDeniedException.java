package com.financesystem.finance_api.modules.platform.subscriptions.domain.exception;

import com.financesystem.finance_api.common.exception.BusinessException;

public class TenantSubscriptionAccessDeniedException extends BusinessException {

    public TenantSubscriptionAccessDeniedException(String message) {
        super(message);
    }
}
