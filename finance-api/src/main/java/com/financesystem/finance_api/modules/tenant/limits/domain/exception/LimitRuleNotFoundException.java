package com.financesystem.finance_api.modules.tenant.limits.domain.exception;

import com.financesystem.finance_api.common.exception.ResourceNotFoundException;

public class LimitRuleNotFoundException extends ResourceNotFoundException {

    public LimitRuleNotFoundException(String message) {
        super(message);
    }
}
