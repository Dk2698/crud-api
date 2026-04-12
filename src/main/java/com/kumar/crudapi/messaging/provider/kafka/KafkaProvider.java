package com.kumar.crudapi.messaging.provider.kafka;

import com.kumar.crudapi.messaging.api.MessagingProvider;
import com.kumar.crudapi.messaging.api.PubSubClient;
import com.kumar.crudapi.messaging.config.ProviderConfig;
import com.kumar.crudapi.messaging.core.registry.SubscriptionRegistry;
import com.kumar.crudapi.messaging.serializer.JsonSerializer;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class KafkaProvider implements MessagingProvider {

    private final SubscriptionRegistry registry;

    @Override
    public String name() {
        return "kafka";
    }

    @Override
    public PubSubClient createClient(ProviderConfig config) {

        Map<String, Object> props = new HashMap<>();

        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                config.getBootstrapServers());

        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                StringSerializer.class);

        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                StringSerializer.class);

        KafkaProducer<String, String> producer =
                new KafkaProducer<>(props);

//        return new KafkaAdapter(producer, registry);
        return null;
    }
}