package com.kumar.crudapi.messaging.provider;

import com.kumar.crudapi.messaging.api.MessagingProvider;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ProviderRegistry {

    private final Map<String, MessagingProvider> providers = new HashMap<>();

    public ProviderRegistry(List<MessagingProvider> providerList) {
        providerList.forEach(p ->
                providers.put(p.name().toLowerCase(), p)
        );
    }

    public MessagingProvider get(String name) {

        MessagingProvider provider = providers.get(name.toLowerCase());

        if (provider == null) {
            throw new RuntimeException("Provider not found: " + name);
        }

        return provider;
    }
}