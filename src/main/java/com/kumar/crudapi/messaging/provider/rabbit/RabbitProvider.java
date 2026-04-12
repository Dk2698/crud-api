package com.kumar.crudapi.messaging.provider.rabbit;

import com.kumar.crudapi.messaging.api.MessagingProvider;
import com.kumar.crudapi.messaging.api.PubSubClient;
import com.kumar.crudapi.messaging.config.ProviderConfig;
import com.kumar.crudapi.messaging.core.registry.SubscriptionRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

//@Component
//@RequiredArgsConstructor
public class RabbitProvider implements MessagingProvider {

//    private final SubscriptionRegistry registry;

    @Override
    public String name() {
        return "rabbit";
    }

    @Override
    public PubSubClient createClient(ProviderConfig config) {

        try {

            String host = config.getHost();
            int port = config.getPort();

            String username = config.getUsername();
            String password = config.getPassword();

            if (host == null) {
                throw new RuntimeException("Missing rabbit.host config");
            }

            // ================= CONNECTION =================

//            CachingConnectionFactory connectionFactory =
//                    new CachingConnectionFactory(host, port);
//
//            if (username != null) {
//                connectionFactory.setUsername(username);
//                connectionFactory.setPassword(password);
//            }
//
//            // ================= TEMPLATE =================
//
//            RabbitTemplate rabbitTemplate =
//                    new RabbitTemplate(connectionFactory);
//
//            // ================= LISTENER =================
//
//            SimpleRabbitListenerContainerFactory factory =
//                    new SimpleRabbitListenerContainerFactory();
//
//            factory.setConnectionFactory(connectionFactory);
//            factory.setConcurrentConsumers(3);
//            factory.setMaxConcurrentConsumers(10);
//
//            return new RabbitAdapter(rabbitTemplate, factory, registry);
            return null;

        } catch (Exception e) {
            throw new RuntimeException("RabbitMQ init failed", e);
        }
    }
}