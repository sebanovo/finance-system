package com.financesystem.finance_api.modules.tenant.reporting.domain.exception;

import com.financesystem.finance_api.common.exception.ResourceNotFoundException;

import java.util.UUID;

public class ReportExportNotFoundException extends ResourceNotFoundException {

    public ReportExportNotFoundException(UUID exportId) {
        super("Report export not found with id: " + exportId);
    }
}
