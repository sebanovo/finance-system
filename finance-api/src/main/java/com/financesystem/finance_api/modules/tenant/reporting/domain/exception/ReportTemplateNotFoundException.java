package com.financesystem.finance_api.modules.tenant.reporting.domain.exception;

import com.financesystem.finance_api.common.exception.ResourceNotFoundException;

public class ReportTemplateNotFoundException extends ResourceNotFoundException {

    public ReportTemplateNotFoundException(String reportType) {
        super("Report template not found for report type: " + reportType);
    }
}
