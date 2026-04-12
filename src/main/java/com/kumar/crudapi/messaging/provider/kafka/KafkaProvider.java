package com.kumar.crudapi.messaging.provider.kafka;

import com.kumar.crudapi.messaging.api.MessagingProvider;
import com.kumar.crudapi.messaging.api.PubSubClient;
import com.kumar.crudapi.messaging.config.MessagingProperties;
import com.kumar.crudapi.messaging.config.ProviderConfig;
import com.kumar.crudapi.messaging.config.SubscriptionConfig;
import com.kumar.crudapi.messaging.core.registry.SubscriptionRegistry;
import com.kumar.crudapi.messaging.serializer.JsonSerializer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaProvider implements MessagingProvider {
    private final MessagingProperties properties;

    private final SubscriptionRegistry registry;

    @Override
    public String name() {
        return "kafka";
    }

    @Override
    public PubSubClient createClient(ProviderConfig config) {

        KafkaTemplate<String, String> template =
                new KafkaTemplate<>(producerFactory(config));

        ConcurrentMessageListenerContainer<String, String> container =
                createContainer(config);

        return new KafkaAdapter(template, container, registry, new JsonSerializer());
    }

    private ProducerFactory<String, String> producerFactory(ProviderConfig config) {

        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, config.getBootstrapServers());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

        return new DefaultKafkaProducerFactory<>(props);
    }

    private ConsumerFactory<String, String> consumerFactory(ProviderConfig config) {

        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, config.getBootstrapServers());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, config.getGroupId());
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        return new DefaultKafkaConsumerFactory<>(props);
    }

    private ConcurrentMessageListenerContainer<String, String> createContainer(
            ProviderConfig config
    ) {

        ConsumerFactory<String, String> factory = consumerFactory(config);

        // load from YAML topics
        List<String> topics = properties.getSubscriptions()
                .values()
                .stream()
                .filter(s -> "kafka".equalsIgnoreCase(s.getProvider()))
                .map(SubscriptionConfig::getDestination)
                .toList();

        ContainerProperties containerProps =
                new ContainerProperties(topics.toArray(new String[0]));


        // ❌ DO NOT set listener here anymore

        ConcurrentMessageListenerContainer<String, String> container =
                new ConcurrentMessageListenerContainer<>(factory, containerProps);

        container.setAutoStartup(false);

        return container;
    }
}