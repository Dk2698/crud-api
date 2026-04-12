package com.kumar.crudapi.messaging.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Data
@Configuration
@ConfigurationProperties(prefix = "messaging")
public class MessagingProperties {

    private String defaultProvider;

    private Map<String, ProviderConfig> providers = new HashMap<>();

    private Map<String, TopicConfig> topics = new HashMap<>();

    private Map<String, SubscriptionConfig> subscriptions = new HashMap<>();
}