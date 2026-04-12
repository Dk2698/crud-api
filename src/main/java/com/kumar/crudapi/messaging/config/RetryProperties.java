package com.kumar.crudapi.messaging.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "messaging.retry")
public class RetryProperties {

    private int maxRetries = 3;
    private long baseDelayMs = 1000;
}