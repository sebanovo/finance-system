package com.financesystem.finance_api.modules.reporting.application.export;

/** Raised when an export artifact cannot be generated. */
public class ReportExportException extends RuntimeException {
    public ReportExportException(String message, Throwable cause) {
        super(message, cause);
    }
}
