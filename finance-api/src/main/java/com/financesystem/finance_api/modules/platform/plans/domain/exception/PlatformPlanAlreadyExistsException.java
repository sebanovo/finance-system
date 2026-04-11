package com.financesystem.finance_api.modules.platform.plans.domain.exception;

import com.financesystem.finance_api.common.exception.BusinessException;

public class PlatformPlanAlreadyExistsException extends BusinessException {

    public PlatformPlanAlreadyExistsException(String message) {
        super(message);
    }
}