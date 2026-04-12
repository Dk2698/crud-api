package com.kumar.crudapi.messaging.core.factory;

import com.kumar.crudapi.messaging.api.PubSubClient;

public interface MessagingFactory {

    PubSubClient getClient(String topicKey);

    PubSubClient getDefaultClient();
}