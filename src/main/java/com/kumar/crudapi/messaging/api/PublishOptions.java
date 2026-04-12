package com.kumar.crudapi.messaging.api;

import lombok.Data;

@Data
public class PublishOptions {

    private int qos = 1;              // MQTT
    private boolean retained = false; // MQTT
    private String key;               // Kafka partition key
}