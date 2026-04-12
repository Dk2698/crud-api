package com.kumar.crudapi.messaging.core.retry;

import com.kumar.crudapi.messaging.config.RetryProperties;
import org.springframework.stereotype.Component;

@Component
public class RetryPolicy {

    private final int maxRetries;
    private final long baseDelayMs;

    public RetryPolicy(RetryProperties properties) {
        this.maxRetries = properties.getMaxRetries();
        this.baseDelayMs = properties.getBaseDelayMs();
    }

    public boolean shouldRetry(int attempt) {
        return attempt <= maxRetries;
    }

    public long getDelay(int attempt) {
        return baseDelayMs * (1L << attempt);
    }
}