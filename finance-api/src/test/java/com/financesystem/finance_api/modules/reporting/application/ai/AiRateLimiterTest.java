package com.financesystem.finance_api.modules.reporting.application.ai;

import com.financesystem.finance_api.modules.reporting.application.config.ReportingProperties;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AiRateLimiterTest {

    private AiRateLimiter limiterWithCap(int cap) {
        ReportingProperties properties = new ReportingProperties();
        properties.getAi().setRateLimitPerMinute(cap);
        return new AiRateLimiter(properties);
    }

    @Test
    void allowsUpToTheCapThenBlocks() {
        AiRateLimiter limiter = limiterWithCap(2);
        assertDoesNotThrow(() -> limiter.check("user-1"));
        assertDoesNotThrow(() -> limiter.check("user-1"));
        assertThrows(RateLimitExceededException.class, () -> limiter.check("user-1"));
    }

    @Test
    void capsAreIndependentPerUser() {
        AiRateLimiter limiter = limiterWithCap(1);
        assertDoesNotThrow(() -> limiter.check("user-1"));
        assertDoesNotThrow(() -> limiter.check("user-2"));
        assertThrows(RateLimitExceededException.class, () -> limiter.check("user-1"));
    }

    @Test
    void zeroOrNegativeCapDisablesLimiting() {
        AiRateLimiter limiter = limiterWithCap(0);
        for (int i = 0; i < 100; i++) {
            assertDoesNotThrow(() -> limiter.check("user-1"));
        }
    }
}
