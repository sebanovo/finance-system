package com.financesystem.finance_api.modules.reporting.application.executor;

/** Raised when a guarded report query fails to execute. */
public class ReportExecutionException extends RuntimeException {
    public ReportExecutionException(String message, Throwable cause) {
        super(message, cause);
    }
}
