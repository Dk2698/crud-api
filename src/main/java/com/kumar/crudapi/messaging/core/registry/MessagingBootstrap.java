package com.kumar.crudapi.messaging.core.registry;

import com.kumar.crudapi.messaging.api.PubSubClient;
import com.kumar.crudapi.messaging.config.MessagingProperties;
import com.kumar.crudapi.messaging.config.ProviderConfig;
import com.kumar.crudapi.messaging.provider.mqtt.MqttProvider;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class MessagingBootstrap {

    private final MqttProvider mqttProvider;
    private final SubscriberAutoRegistrar registrar;
    private final MessagingProperties properties;

    @PostConstruct
    public void init() {

        log.info("🚀 Messaging system starting...");

        registrar.registerAll();

        log.info("✅ Messaging system ready");
    }

}