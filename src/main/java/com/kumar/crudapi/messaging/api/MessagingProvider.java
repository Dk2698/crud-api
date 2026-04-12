package com.kumar.crudapi.messaging.api;

import com.kumar.crudapi.messaging.config.ProviderConfig;

public interface MessagingProvider {

    String name();

    PubSubClient createClient(ProviderConfig config);

//    PubSubClient getClient(MessagingProvider provider);
}