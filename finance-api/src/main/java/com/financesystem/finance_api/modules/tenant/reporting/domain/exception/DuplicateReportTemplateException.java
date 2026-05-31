package com.financesystem.finance_api.modules.tenant.reporting.domain.exception;

import com.financesystem.finance_api.common.exception.BusinessException;

public class DuplicateReportTemplateException extends BusinessException {

    public DuplicateReportTemplateException(String reportType) {
        super("Duplicate report template registered for report type: " + reportType);
    }
}
