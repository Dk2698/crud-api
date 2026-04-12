package com.kumar.crudapi.messaging.config;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class TopicConfig {

    private String provider;   // mqtt | kafka | rabbit | gcp
    private String destination;

    private Map<String, Object> properties = new HashMap<>();
}