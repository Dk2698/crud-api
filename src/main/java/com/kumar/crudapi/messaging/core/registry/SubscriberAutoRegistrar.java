package com.kumar.crudapi.messaging.core.registry;

import com.kumar.crudapi.messaging.api.MessageHandler;
import com.kumar.crudapi.messaging.api.MessagingProvider;
import com.kumar.crudapi.messaging.api.MqttSubscriber;
import com.kumar.crudapi.messaging.api.PubSubClient;
import com.kumar.crudapi.messaging.config.MessagingProperties;
import com.kumar.crudapi.messaging.config.ProviderConfig;
import com.kumar.crudapi.messaging.config.SubscriptionConfig;
import com.kumar.crudapi.messaging.config.TopicConfig;
import com.kumar.crudapi.messaging.core.cache.MessagingClientCache;
import com.kumar.crudapi.messaging.core.factory.MessagingFactory;
import com.kumar.crudapi.messaging.provider.ProviderRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class SubscriberAutoRegistrar {

    private final List<MessageHandler> handlers;
    private final MessagingProperties properties;
    private final ProviderRegistry providerRegistry;
    private final MessagingClientCache clientCache;


    public void registerAll() {

        for (MessageHandler handler : handlers) {

            MqttSubscriber ann =
                    handler.getClass().getAnnotation(MqttSubscriber.class);

            if (ann == null) continue;

            String topicKey = ann.topicKey();

            // 1️⃣ subscription config
            SubscriptionConfig subscription =
                    properties.getSubscriptions().get(topicKey);

            if (subscription == null) {
                throw new RuntimeException("Subscription not found: " + topicKey);
            }

            // 2️⃣ provider
            String providerName = subscription.getProvider();

            if (providerName == null) {
                providerName = properties.getDefaultProvider();
            }

            // 3️⃣ provider config
            ProviderConfig providerConfig =
                    properties.getProviders().get(providerName);

            // 4️⃣ REAL CLIENT (IMPORTANT FIX)
            MessagingProvider provider =
                    providerRegistry.get(providerName);

//            PubSubClient client =
//                    provider.createClient(providerConfig);
            PubSubClient client = clientCache.getOrCreate(
                    providerName,
                    providerConfig,
                    provider
            );

            // 5️⃣ subscribe
            client.subscribe(subscription.getDestination(), handler);

            log.info("✅ SUBSCRIBED");
            log.info(" topicKey={}", topicKey);
            log.info(" provider={}", providerName);
            log.info(" destination={}", subscription.getDestination());
        }
    }
}