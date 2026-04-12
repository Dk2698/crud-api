package com.kumar.crudapi.messaging.api;

import java.util.concurrent.CompletableFuture;

public interface PubSubClient {

    void publish(Message message);

    CompletableFuture<Void> publishAsync(Message message);

    void publish(Message message, PublishOptions options);

    void subscribe(String topic, MessageHandler handler);

    void subscribe(String topic, MessageHandler handler, SubscribeOptions options);

    void unsubscribe(String topic);

    void close();
}