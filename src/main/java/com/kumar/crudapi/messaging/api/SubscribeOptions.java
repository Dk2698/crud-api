package com.kumar.crudapi.messaging.api;

import lombok.Data;

@Data
public class SubscribeOptions {

    private int qos = 1;           // MQTT
    private String groupId;        // Kafka consumer group
    private boolean autoAck = true;
}