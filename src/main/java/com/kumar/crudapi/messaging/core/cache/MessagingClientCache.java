package com.kumar.crudapi.messaging.core.cache;

import com.kumar.crudapi.messaging.api.MessagingProvider;
import com.kumar.crudapi.messaging.api.PubSubClient;
import com.kumar.crudapi.messaging.config.ProviderConfig;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class MessagingClientCache {

    private final Map<String, PubSubClient> cache = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        log.info("ClientCache initialized");
    }

    public PubSubClient getOrCreate(
            String providerName,
            ProviderConfig config,
            MessagingProvider provider
    ) {

        String key = buildKey(providerName, config);

        return cache.computeIfAbsent(key, k -> {
            log.info("🚀 Creating new client: {}", key);
            return provider.createClient(config);
        });
    }

    private String buildKey(String providerName, ProviderConfig config) {
        return providerName + "::"
                + config.getBrokerUrl()
                + "::"
                + config.getClientId();
    }

//    public PubSubClient get(String key) {
//        return cache.get(key);
//    }
//
//    public PubSubClient put(String key, PubSubClient client) {
//        cache.put(key, client);
//        return client;
//    }
//
//    public void remove(String key) {
//        cache.remove(key);
//    }
//
//    public void clear() {
//        cache.clear();
//    }

    @PreDestroy
    public void shutdown() {
        cache.clear();
    }
}