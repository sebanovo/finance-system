package com.financesystem.finance_api.modules.reporting.application.ai;

/** Raised when a user exceeds the AI request rate limit; mapped to HTTP 429. */
public class RateLimitExceededException extends RuntimeException {
    public RateLimitExceededException(String message) {
        super(message);
    }
}
