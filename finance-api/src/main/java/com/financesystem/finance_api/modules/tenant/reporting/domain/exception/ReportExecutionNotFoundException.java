package com.financesystem.finance_api.modules.tenant.reporting.domain.exception;

import com.financesystem.finance_api.common.exception.ResourceNotFoundException;

import java.util.UUID;

public class ReportExecutionNotFoundException extends ResourceNotFoundException {

    public ReportExecutionNotFoundException(UUID executionId) {
        super("Report execution not found with id: " + executionId);
    }
}
