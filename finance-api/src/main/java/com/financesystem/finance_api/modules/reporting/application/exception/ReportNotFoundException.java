package com.financesystem.finance_api.modules.reporting.application.exception;

/** A report definition, execution or export was not found. */
public class ReportNotFoundException extends RuntimeException {
    public ReportNotFoundException(String message) {
        super(message);
    }
}
