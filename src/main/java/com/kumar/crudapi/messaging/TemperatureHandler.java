package com.kumar.crudapi.messaging;

import com.kumar.crudapi.messaging.api.Message;
import com.kumar.crudapi.messaging.api.MessageHandler;
import com.kumar.crudapi.messaging.api.MqttSubscriber;
import org.springframework.stereotype.Component;

@Component
@MqttSubscriber(topicKey = "sensor-data")
public class TemperatureHandler implements MessageHandler {
    @Override
    public void handle(Message message) {
        System.out.println("🔥 TEMP HANDLER: " + message.getPayload());
    }
}