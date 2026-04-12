package com.kumar.crudapi.messaging.core.dlq;

import com.kumar.crudapi.messaging.api.Message;
import com.kumar.crudapi.messaging.api.PubSubClient;
import com.kumar.crudapi.messaging.core.factory.MessagingFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class DeadLetterPublisher {

    private final MessagingFactory factory;

    public void publish(String topic, Message message, Exception e) {

        PubSubClient client = factory.getClient(topic);

        Map<String, Object> dlqPayload = new HashMap<>();
        dlqPayload.put("originalTopic", topic);
        dlqPayload.put("payload", message.getPayload());
        dlqPayload.put("error", e.getMessage());
        dlqPayload.put("timestamp", System.currentTimeMillis());

        Message dlqMessage = new Message(
                topic + ".DLQ",
                dlqPayload
        );

        client.publish(dlqMessage);
    }
}