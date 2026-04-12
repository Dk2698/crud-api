package com.kumar.crudapi.messaging;

import com.kumar.crudapi.messaging.api.Message;
import com.kumar.crudapi.messaging.api.PubSubClient;
import com.kumar.crudapi.messaging.config.MessagingProperties;
import com.kumar.crudapi.messaging.config.TopicConfig;
import com.kumar.crudapi.messaging.core.factory.MessagingFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class MessagingTemplate {

    private final MessagingFactory factory;
    private final MessagingProperties properties;

    public void publish(String topicKey, Object payload) {

        TopicConfig topic = getTopic(topicKey);

        PubSubClient client = factory.getClient(topicKey);

        Message message = new Message();
        message.setTopic(topic.getDestination());
        message.setPayload(payload);
        message.setTimestamp(System.currentTimeMillis());

        // optional metadata
        message.setHeaders(Map.of(
                "type", payload.getClass().getName()
        ));

        client.publish(message);
    }

    public void publishAsync(String topicKey, Object payload) {

        TopicConfig topic = getTopic(topicKey);

        PubSubClient client = factory.getClient(topicKey);

        Message message = new Message();
        message.setTopic(topic.getDestination());
        message.setPayload(payload);

        client.publishAsync(message);
    }

    private TopicConfig getTopic(String topicKey) {

        TopicConfig topic = properties.getTopics().get(topicKey);

        if (topic == null) {
            throw new RuntimeException("Topic not found: " + topicKey);
        }

        return topic;
    }
}