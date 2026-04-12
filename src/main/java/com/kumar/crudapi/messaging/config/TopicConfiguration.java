package com.kumar.crudapi.messaging.config;

import com.kumar.crudapi.exception.BadConfigurationException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@ConfigurationProperties(prefix = "kumat.messaging")
@Data
@Slf4j
public class TopicConfiguration {

    private Map<String, TopicConfig> topics;
    private Map<String, SubscriptionConfig> subscriptions;

    public TopicConfig getTopic(String topic) {
        TopicConfig config = topics.get(topic);

        if (config == null) {
            throw new BadConfigurationException(
                    "missing.configuration",
                    "Missing topic config: " + topic,
                    "kumar.messaging.topics." + topic
            );
        }

        return config;
    }

    public SubscriptionConfig getSubscription(String name) {
        SubscriptionConfig config = subscriptions.get(name);

        if (config == null) {
            throw new BadConfigurationException(
                    "missing.configuration",
                    "Missing subscription config: " + name,
                    "kumar.messaging.subscriptions." + name
            );
        }

        return config;
    }
}