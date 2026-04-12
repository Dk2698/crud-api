package com.kumar.crudapi.controllers;

import com.kumar.crudapi.messaging.MessagingTemplate;
import com.kumar.crudapi.messaging.SensorData;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/messaging")
@RequiredArgsConstructor
public class MqttTestController {

    private final MessagingTemplate messagingTemplate;

//    @PostMapping("/publish/{topic}")
//    public String publish(@PathVariable String topic,
//                          @RequestBody Map<String, Object> body) {
//
//        messagingTemplate.publish(topic, body);
//
//        return "sent to " + topic;
//    }

    @PostMapping("/publish")
    public String publish(@RequestBody SensorData data) {

        messagingTemplate.publish("sensor-data", data);

        return "sent: " + data;
    }
}