package com.kumar.crudapi.messaging.provider.mqtt;

import com.kumar.crudapi.messaging.api.*;
import com.kumar.crudapi.messaging.core.registry.SubscriptionRegistry;
import com.kumar.crudapi.messaging.serializer.HandlerTypeResolver;
import com.kumar.crudapi.messaging.serializer.JsonSerializer;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.*;

import java.util.concurrent.CompletableFuture;


//@Component
@Slf4j
public class MqttAdapter implements PubSubClient {

    private final MqttClient client;
    private final SubscriptionRegistry registry;
    private final JsonSerializer serializer;
    private final HandlerTypeResolver handlerTypeResolver;

    public MqttAdapter(MqttClient client,
                       SubscriptionRegistry registry, JsonSerializer serializer) throws MqttException {

        this.client = client;
        this.registry = registry;
        this.serializer = serializer;
        this.handlerTypeResolver = new HandlerTypeResolver();
        initCallback();
    }

    // ================= CALLBACK (ONLY ONCE) =================

    private void initCallback() {

        client.setCallback(new MqttCallback() {

            @Override
            public void connectionLost(Throwable cause) {
                log.error("❌ MQTT connection lost", cause);
                new Thread(MqttAdapter.this::reconnectAndResubscribe).start();
            }

            @Override
            public void messageArrived(String topic, MqttMessage mqttMessage) {

                // 🔥 async processing (non-blocking MQTT thread)
                CompletableFuture.runAsync(() ->
                        processMessage(topic, mqttMessage)
                );
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                log.debug("✅ Delivery complete: {}", token.getMessageId());
            }
        });
    }

    private void processMessage(String topic, MqttMessage mqttMessage) {

        String json = new String(mqttMessage.getPayload());

        log.debug("📩 Incoming message | topic={} payload={}", topic, json);

        MessageHandler<?> handler = registry.get(topic);

        if (handler == null) {
            log.warn("⚠️ No handler registered for topic: {}", topic);
            return;
        }

        Message message = new Message(topic, json);

        try {
            // 🔥 resolve handler generic type
            Class<?> payloadType = handlerTypeResolver.resolve(handler);

            // 🔥 deserialize JSON → object
            Object payload = serializer.fromJson(json, payloadType);

            log.debug("✅ Deserialized payload type={}", payloadType.getSimpleName());

            // 🔥 invoke handler
            invokeHandler(handler, payload, message);

        } catch (Exception e) {
            handleError(handler, message, e);
        }
    }

    @SuppressWarnings("unchecked")
    private void invokeHandler(MessageHandler<?> handler, Object payload, Message message) {

        try {
            ((MessageHandler<Object>) handler).handle(payload, message);

        } catch (Exception e) {
            handleError(handler, message, e);
        }
    }

    private void handleError(MessageHandler<?> handler, Message message, Exception e) {

        log.error("❌ Message processing failed | topic={} payload={}",
                message.getTopic(),
                message.getPayload(),
                e
        );

        try {
            handler.onError(message, e);

            if (handler.retryOnError()) {
                log.warn("🔁 Retrying message for topic={}", message.getTopic());

                // 🔥 simple retry (you can enhance later)
                invokeHandler(handler, message.getPayload(), message);
            }

        } catch (Exception ex) {
            log.error("❌ Error during retry handling", ex);
        }
    }

    private void reconnectAndResubscribe() {

        while (!client.isConnected()) {
            try {
                Thread.sleep(3000);
                client.reconnect();

                registry.getAll().forEach((topic, handler) -> {
                    try {
                        client.subscribe(topic);
                        log.info("Re-subscribed: {}", topic);
                    } catch (Exception e) {
                        log.error("Resubscribe failed", e);
                    }
                });

            } catch (Exception ignored) {
            }
        }
    }

    // ================= PUBLISH =================

    @Override
    public void publish(Message message) {
        publish(message, new PublishOptions());
    }

    @Override
    public void publish(Message message, PublishOptions options) {

        try {

//            MqttMessage mqttMessage = new MqttMessage(
//                    message.getPayload().toString().getBytes()
//            );

            String json = serializer.toJson(message.getPayload());

            MqttMessage mqttMessage = new MqttMessage(json.getBytes());

            mqttMessage.setQos(options.getQos());
            mqttMessage.setRetained(options.isRetained());

            client.publish(message.getTopic(), mqttMessage);

        } catch (Exception e) {
            throw new RuntimeException("MQTT publish failed", e);
        }
    }

    @Override
    public CompletableFuture<Void> publishAsync(Message message) {
        return CompletableFuture.runAsync(() -> publish(message));
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

        try {

            // 🔥 register centrally
            registry.register(topic, handler);

            client.subscribe(topic, options.getQos());

            System.out.println("Subscribed to: " + topic);

        } catch (Exception e) {
            throw new RuntimeException("MQTT subscribe failed", e);
        }
    }

    @Override
    public void unsubscribe(String topic) {

        try {
            registry.remove(topic);
            client.unsubscribe(topic);

        } catch (Exception e) {
            throw new RuntimeException("MQTT unsubscribe failed", e);
        }
    }

    // ================= CLOSE =================

    @Override
    public void close() {

        try {
            if (client.isConnected()) {
                client.disconnect();
            }
            client.close();

        } catch (Exception e) {
            throw new RuntimeException("MQTT close failed", e);
        }
    }
}
