package com.financesystem.finance.modules.platform.plans.domain.exception;

import com.financesystem.finance.common.exception.BusinessException;

public class PlatformPlanAlreadyExistsException extends BusinessException {

    public PlatformPlanAlreadyExistsException(String message) {
        super(message);
    }
}