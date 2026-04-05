package com.financesystem.finance.modules.platform.subscriptions.domain.exception;

import com.financesystem.finance.common.exception.ResourceNotFoundException;

public class PlatformSubscriptionNotFoundException extends ResourceNotFoundException {

    public PlatformSubscriptionNotFoundException(String message) {
        super(message);
    }
}
