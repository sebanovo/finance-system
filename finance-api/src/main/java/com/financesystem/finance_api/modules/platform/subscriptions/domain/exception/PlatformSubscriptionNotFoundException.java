package com.financesystem.finance_api.modules.platform.subscriptions.domain.exception;

import com.financesystem.finance_api.common.exception.ResourceNotFoundException;

public class PlatformSubscriptionNotFoundException extends ResourceNotFoundException {

    public PlatformSubscriptionNotFoundException(String message) {
        super(message);
    }
}
