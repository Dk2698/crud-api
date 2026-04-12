package com.kumar.crudapi.messaging.config;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class ProviderConfig {

    private String brokerUrl;
    private String clientId;
    private String username;
    private String password;

    private String bootstrapServers;
    private String groupId;

    private String host;
    private Integer port;
}