package com.financesystem.finance_api.modules.tenant.reporting.domain.exception;

import com.financesystem.finance_api.common.exception.BusinessException;

import java.util.UUID;

public class ReportExportContentUnavailableException extends BusinessException {

    public ReportExportContentUnavailableException(UUID exportId) {
        super("Report export content is not available for export id: " + exportId);
    }
}
