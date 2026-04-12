package com.kumar.crudapi.messaging.provider.kafka;

import com.kumar.crudapi.messaging.api.*;
import com.kumar.crudapi.messaging.core.registry.SubscriptionRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;

import org.springframework.kafka.listener.*;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.*;

@Slf4j
public class KafkaAdapter implements PubSubClient {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ConcurrentMessageListenerContainer<String, String> container;
    private final SubscriptionRegistry registry;

    public KafkaAdapter(KafkaTemplate<String, String> kafkaTemplate,
                        ConcurrentMessageListenerContainer<String, String> container,
                        SubscriptionRegistry registry) {

        this.kafkaTemplate = kafkaTemplate;
        this.container = container;
        this.registry = registry;

        setupListener();
    }

    // ================= CONSUMER =================

    private void setupListener() {

        container.setupMessageListener((MessageListener<String, String>) record -> {

            String topic = record.topic();

            MessageHandler handler = registry.get(topic);

            if (handler != null) {

                Message message = new Message(topic, record.value());

                CompletableFuture.runAsync(() -> {
                    try {
                        handler.handle(message);
                    } catch (Exception e) {
                        handler.onError(message, e);
                        log.error("Kafka message failed: {}", topic, e);
                    }
                });
            }
        });
    }

    // ================= PUBLISH =================

    @Override
    public void publish(Message message) {
        publish(message, new PublishOptions());
    }

    @Override
    public void publish(Message message, PublishOptions options) {

        try {

            if (options.getKey() != null) {
                kafkaTemplate.send(
                        message.getTopic(),
                        options.getKey(),
                        message.getPayload().toString()
                );
            } else {
                kafkaTemplate.send(
                        message.getTopic(),
                        message.getPayload().toString()
                );
            }

        } catch (Exception e) {
            throw new RuntimeException("Kafka publish failed", e);
        }
    }

    @Override
    public CompletableFuture<Void> publishAsync(Message message) {

        return kafkaTemplate.send(message.getTopic(),
                        message.getPayload().toString())
//                .completable()
                .thenAccept(r -> {})
                .exceptionally(ex -> {
                    throw new RuntimeException("Kafka async publish failed", ex);
                });
    }

    // ================= SUBSCRIBE =================

    @Override
    public void subscribe(String topic, MessageHandler handler) {
        subscribe(topic, handler, new SubscribeOptions());
    }

    @Override
    public void subscribe(String topic,
                          MessageHandler handler,
                          SubscribeOptions options) {

        registry.register(topic, handler);

        // 🔥 dynamic topic add
//        container.getContainerProperties().setTopics(topic);

        if (!container.isRunning()) {
            container.start();
        }

        log.info("Kafka subscribed to: {}", topic);
    }

    @Override
    public void unsubscribe(String topic) {
        registry.remove(topic);
        log.info("Kafka unsubscribed from: {}", topic);
    }

    // ================= CLOSE =================

    @Override
    public void close() {

        try {
            container.stop();
            log.info("Kafka container stopped");
        } catch (Exception e) {
            throw new RuntimeException("Kafka close failed", e);
        }
    }
}