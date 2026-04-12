package com.kumar.crudapi.messaging.config;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class SubscriptionConfig {

    private String provider;
    private String destination;
    private String routing;

    private Map<String, Object> properties = new HashMap<>();
}