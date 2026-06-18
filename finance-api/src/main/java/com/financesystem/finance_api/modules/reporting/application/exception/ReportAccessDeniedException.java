package com.financesystem.finance_api.modules.reporting.application.exception;

/** The caller may not access this execution/export (wrong scope or tenant). */
public class ReportAccessDeniedException extends RuntimeException {
    public ReportAccessDeniedException(String message) {
        super(message);
    }
}
