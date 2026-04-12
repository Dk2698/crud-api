package com.kumar.crudapi.messaging.api;

public interface MessageHandler {

    void handle(Message message) throws Exception;

    default void onError(Message message, Exception e) {}

    default boolean retryOnError() {
        return true;
    }
}