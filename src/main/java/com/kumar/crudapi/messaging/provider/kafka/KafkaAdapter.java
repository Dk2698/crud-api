package com.kumar.crudapi.messaging.provider.kafka;

import com.kumar.crudapi.messaging.api.*;
import com.kumar.crudapi.messaging.core.registry.SubscriptionRegistry;
import com.kumar.crudapi.messaging.serializer.HandlerTypeResolver;
import com.kumar.crudapi.messaging.serializer.JsonSerializer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.support.SendResult;

import java.util.concurrent.CompletableFuture;

@Slf4j
public class KafkaAdapter implements PubSubClient {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ConcurrentMessageListenerContainer<String, String> container;
    private final SubscriptionRegistry registry;
    private final JsonSerializer serializer;
    private final HandlerTypeResolver handlerTypeResolver;

    public KafkaAdapter(KafkaTemplate<String, String> kafkaTemplate,
                        ConcurrentMessageListenerContainer<String, String> container,
                        SubscriptionRegistry registry, JsonSerializer serializer) {

        this.kafkaTemplate = kafkaTemplate;
        this.container = container;
        this.registry = registry;
        this.serializer = serializer;
        this.handlerTypeResolver = new HandlerTypeResolver();

        initListener();
    }

    // ================= CONSUMER =================

    private void initListener() {

        ContainerProperties props = container.getContainerProperties();

        props.setMessageListener((MessageListener<String, String>) record -> {

            String topic = record.topic();
            String value = record.value();
            log.info("topic: {}, value: {}", topic, value);
            log.info("topic: {}, value: {}", topic, value.toString());
            log.info("topic: {}, value: {}", topic, value.getBytes());

            long timestamp = record.timestamp();   // ✅ HERE

            MessageHandler handler = registry.get(topic);

            if (handler == null) {
                log.warn("⚠️ No handler for topic: {}", topic);
                return;
            }
//            Class<?> payloadType = handlerTypeResolver.resolve(handler);
//            Object payload = serializer.fromBytes(bytes, payloadType);
            String json = record.value();

            Class<?> payloadType = handlerTypeResolver.resolve(handler);

            Object payload = serializer.fromJson(json, payloadType);

            Message message = new Message(topic, payload);

            CompletableFuture.runAsync(() -> {
                try {
//                    handler.handle(message);
                    ((MessageHandler<Object>) handler).handle(payload, message);

                } catch (Exception e) {
                    handler.onError(message, e);
                    log.error("Kafka error topic={}", topic, e);
                }
            });
        });

        container.start();
    }

    // ================= PUBLISH =================

    @Override
    public void publish(Message message) {
        publish(message, new PublishOptions());
    }

    @Override
    public void publish(Message message, PublishOptions options) {

        try {

            String topic = message.getTopic();
            String json = serializer.toJson(message.getPayload());

            CompletableFuture<SendResult<String, String>> future;

            if (options.getKey() != null) {
                future = kafkaTemplate.send(topic, options.getKey(), json);
            } else {
                future = kafkaTemplate.send(topic, json);
            }

            future.whenComplete((result, ex) -> {

                if (ex != null) {
                    log.error("❌ Kafka publish failed topic={}", topic, ex);
                    return;
                }

                log.debug("📤 Kafka published → topic={}, partition={}, offset={}, timestamp={}",
                        topic,
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset(),
                        result.getRecordMetadata().timestamp()
                );
            });

        } catch (Exception e) {
            throw new RuntimeException("Kafka publish failed", e);
        }
    }

    @Override
    public CompletableFuture<Void> publishAsync(Message message) {

        CompletableFuture<SendResult<String, String>> future =
                kafkaTemplate.send(
                        message.getTopic(),
                        message.getPayload().toString()
                );

        return future
                .thenAccept(result -> {
                    log.debug("📤 Async published → topic={}, offset={}",
                            message.getTopic(),
                            result.getRecordMetadata().offset()
                    );
                })
                .exceptionally(ex -> {
                    log.error("❌ Async Kafka publish failed topic={}",
                            message.getTopic(), ex);
                    return null;
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

        // ✅ Kafka correct method
//        container.addTopics(topic);
        if (!container.isRunning()) {
            container.start();
        }

        log.info("Kafka subscribed to: {}", topic);
    }

    @Override
    public void unsubscribe(String topic) {
        registry.remove(topic);
        container.stop(); // Kafka doesn't support true dynamic remove cleanly
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