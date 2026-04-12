package com.kumar.crudapi.messaging.api;

import lombok.Data;

import java.util.Map;

@Data
public class Message {

    private String topic;              // common across all systems
    private Object payload;            // actual data
    private Map<String, Object> headers;
    private long timestamp;

    public Message() {
    }

    public Message(String topic, Object payload) {
        this.topic = topic;
        this.payload = payload;
        this.timestamp = System.currentTimeMillis();
    }

}