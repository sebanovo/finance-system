package com.financesystem.finance_api.modules.tenant.reporting.domain.exception;

import com.financesystem.finance_api.common.exception.BusinessException;

public class ReportExecutionPayloadException extends BusinessException {

    public ReportExecutionPayloadException(String message) {
        super(message);
    }

    public ReportExecutionPayloadException(String message, Throwable cause) {
        super(message, cause);
    }
}
