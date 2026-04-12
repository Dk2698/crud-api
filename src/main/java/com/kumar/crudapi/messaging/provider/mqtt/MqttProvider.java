package com.kumar.crudapi.messaging.provider.mqtt;

import com.kumar.crudapi.messaging.api.MessagingProvider;
import com.kumar.crudapi.messaging.api.PubSubClient;
import com.kumar.crudapi.messaging.config.ProviderConfig;
import com.kumar.crudapi.messaging.core.registry.SubscriptionRegistry;
import com.kumar.crudapi.messaging.provider.ProviderRegistry;
import com.kumar.crudapi.messaging.serializer.JsonSerializer;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.stereotype.Component;


@Slf4j
@Component
@RequiredArgsConstructor
public class MqttProvider implements MessagingProvider {

    private final SubscriptionRegistry registry;

    @Override
    public String name() {
        return "mqtt";
    }

    @Override
    public PubSubClient createClient(ProviderConfig config) {

        try {
            String brokerUrl = config.getBrokerUrl();

            if (brokerUrl == null) {
                throw new RuntimeException("Missing mqtt.broker-url");
            }

            String clientId = config.getClientId();
//                    .getOrDefault("client-id", "mqtt-client");

            log.info("Connecting MQTT: {}", brokerUrl);

            MqttClient client = new MqttClient(
                    brokerUrl,
                    clientId,
                    new MemoryPersistence()
            );

            MqttConnectOptions options = new MqttConnectOptions();
            options.setAutomaticReconnect(false);
            options.setCleanSession(true);
            options.setConnectionTimeout(10);
            options.setKeepAliveInterval(60);

            String username = config.getUsername();
            String password = config.getPassword();

            if (username != null && !username.isEmpty()) {
                options.setUserName(username);
                options.setPassword(password.toCharArray());
            }

            client.connect(options);

            return new MqttAdapter(client, registry, new JsonSerializer());

        } catch (Exception e) {
            throw new RuntimeException("MQTT init failed", e);
        }
    }
}