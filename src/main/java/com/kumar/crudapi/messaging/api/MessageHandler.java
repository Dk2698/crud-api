package com.kumar.crudapi.messaging.api;

public interface MessageHandler<T> {

    void handle(Message message) throws Exception;

    default void onError(Message message, Exception e) {}

    default boolean retryOnError() {
        return true;
    }

    void handle(T payload, Message message);
}

//public interface MessageHandler<T> {
//
//    void handle(T payload, Message message);
//
//    default void onError(Message message, Exception e) {
//        e.printStackTrace();
//    }
//
//    default boolean retryOnError() {
//        return true;
//    }
//}