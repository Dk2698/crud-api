package com.kumar.crudapi.controllers;

import com.kumar.crudapi.messaging.MessagingTemplate;
import com.kumar.crudapi.messaging.SensorData;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/messaging")
@RequiredArgsConstructor
public class KafkaTestController {

    private final MessagingTemplate messagingTemplate;

    @PostMapping("/kafka")
    public String sendKafka() {

        SensorData data = new SensorData();
        data.setSensorId("S1");
        data.setSensorName("Temp Sensor");
        data.setSensorValue(25.5);
        // order-events -> topicKey in yml
        messagingTemplate.publish("order-events", data);

        return "sent to kafka";
    }
}