package com.financesystem.finance_api.modules.reporting.application.ai;

/** Raised when the NL→SQL microservice is unreachable; controllers map this to 503. */
public class ReportsAiUnavailableException extends RuntimeException {
    public ReportsAiUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
