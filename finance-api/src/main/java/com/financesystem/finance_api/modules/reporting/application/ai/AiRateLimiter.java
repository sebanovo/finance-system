package com.financesystem.finance_api.modules.reporting.application.ai;

import com.financesystem.finance_api.modules.reporting.application.config.ReportingProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Per-user, in-memory sliding-window rate limiter for the AI rails. First line
 * of defense in finance-api (the microservice has its own coarse limiter).
 * Not shared across replicas — fine for a single-instance deployment.
 */
@Component
public class AiRateLimiter {

    private static final long WINDOW_MS = 60_000L;

    private final ReportingProperties properties;
    private final Map<String, Deque<Long>> hits = new ConcurrentHashMap<>();

    public AiRateLimiter(ReportingProperties properties) {
        this.properties = properties;
    }

    /** Records a hit for {@code key}; throws {@link RateLimitExceededException} if over the cap. */
    public void check(String key) {
        int max = properties.getAi().getRateLimitPerMinute();
        if (max <= 0) {
            return;
        }
        long now = System.currentTimeMillis();
        Deque<Long> timestamps = hits.computeIfAbsent(key == null ? "anonymous" : key, k -> new ArrayDeque<>());
        synchronized (timestamps) {
            while (!timestamps.isEmpty() && timestamps.peekFirst() < now - WINDOW_MS) {
                timestamps.pollFirst();
            }
            if (timestamps.size() >= max) {
                throw new RateLimitExceededException(
                        "Límite de solicitudes de IA alcanzado. Esperá un momento e intentá de nuevo.");
            }
            timestamps.addLast(now);
        }
    }
}
