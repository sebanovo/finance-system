package com.financesystem.finance_api.modules.reporting.application.guard;

/** Raised when SQL (from the LLM or a controlled template) violates the guard. */
public class SqlGuardException extends RuntimeException {
    public SqlGuardException(String message) {
        super(message);
    }
}
