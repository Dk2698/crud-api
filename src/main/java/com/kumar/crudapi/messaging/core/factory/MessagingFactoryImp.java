package com.kumar.crudapi.messaging.core.factory;

import com.kumar.crudapi.messaging.api.MessagingProvider;
import com.kumar.crudapi.messaging.api.PubSubClient;
import com.kumar.crudapi.messaging.config.MessagingProperties;
import com.kumar.crudapi.messaging.config.ProviderConfig;
import com.kumar.crudapi.messaging.config.TopicConfig;
import com.kumar.crudapi.messaging.core.cache.MessagingClientCache;
import com.kumar.crudapi.messaging.provider.ProviderRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class MessagingFactoryImp implements MessagingFactory {

    private final MessagingProperties properties;
    private final ProviderRegistry registry;
    private final MessagingClientCache clientCache;

    private final Map<String, PubSubClient> cache = new ConcurrentHashMap<>();

    @Override
    public PubSubClient getClient(String topicKey) {

        TopicConfig topic = properties.getTopics().get(topicKey);

        if (topic == null) {
            throw new RuntimeException("Topic not found: " + topicKey);
        }

        String providerName = topic.getProvider() != null
                ? topic.getProvider()
                : properties.getDefaultProvider();

        ProviderConfig providerConfig =
                properties.getProviders().get(providerName);

        if (providerConfig == null) {
            throw new RuntimeException("Missing provider config: " + providerName);
        }
        MessagingProvider provider =
                registry.get(providerName);

        return clientCache.getOrCreate(providerName, providerConfig, provider);
    }

    @Override
    public PubSubClient getDefaultClient() {

        String providerName = properties.getDefaultProvider();

        ProviderConfig config = properties.getProviders().get(providerName);

        MessagingProvider provider = registry.get(providerName);

        return provider.createClient(config);
    }
}