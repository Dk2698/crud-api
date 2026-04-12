package com.kumar.crudapi.messaging.service;

import com.kumar.crudapi.messaging.SensorData;
import com.kumar.crudapi.messaging.api.Message;
import com.kumar.crudapi.messaging.api.MessageHandler;
import com.kumar.crudapi.messaging.api.MqttSubscriber;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@MqttSubscriber(topicKey = "sensor-data", type = SensorData.class)
@Slf4j
public class TemperatureHandler implements MessageHandler<SensorData> {

    @Override
    public void handle(Message message) {
        log.debug("🔥 TEMP HANDLER: " + message.getPayload());
    }


    @Override
    public void handle(SensorData payload, Message message) {
        log.debug("🔥 SENSOR ID: {}", payload.getSensorId());
        log.debug("🔥 VALUE: {}", payload.getSensorValue());
    }

    @Override
    public void onError(Message message, Exception e) {
        log.error("Handler error for topic={}", message.getTopic(), e);
    }
}