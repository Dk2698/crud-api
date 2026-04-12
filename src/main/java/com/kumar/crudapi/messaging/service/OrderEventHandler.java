package com.kumar.crudapi.messaging.service;

import com.kumar.crudapi.messaging.SensorData;
import com.kumar.crudapi.messaging.api.Message;
import com.kumar.crudapi.messaging.api.MessageHandler;
import com.kumar.crudapi.messaging.api.MqttSubscriber;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@MqttSubscriber(topicKey = "order-events", type = SensorData.class)
@Slf4j
public class OrderEventHandler implements MessageHandler<SensorData> {

    @Override
    public void handle(Message message) {

        System.out.println("Processing order: " + message.getPayload());

        // simulate failure
        if (message.getPayload().toString().contains("fail")) {
            throw new RuntimeException("Order processing failed");
        }
    }

    @Override
    public void onError(Message message, Exception e) {
        log.debug("Handler error: " + e.getMessage());
    }

    @Override
    public boolean retryOnError() {
        return true;
    }

    @Override
    public void handle(SensorData payload, Message message) {
        log.debug("🔥 order ID: {}", payload.getSensorId());
        log.debug("🔥 order: {}", payload.getSensorValue());
    }
}