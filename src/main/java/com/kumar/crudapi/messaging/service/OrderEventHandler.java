package com.kumar.crudapi.messaging.service;

import com.kumar.crudapi.messaging.api.Message;
import com.kumar.crudapi.messaging.api.MessageHandler;
import org.springframework.stereotype.Component;

@Component
public class OrderEventHandler implements MessageHandler {

    @Override
    public void handle(Message message) {

        System.out.println("Processing order: " + message.getPayload());

        // simulate failure
        if (message.getPayload().toString().contains("fail")) {
            throw new RuntimeException("Order processing failed");
        }
    }

    @Override
    public void onError(Message message, Exception e) {
        System.out.println("Handler error: " + e.getMessage());
    }

    @Override
    public boolean retryOnError() {
        return true;
    }

    @Override
    public void handle(Object payload, Message message) {

    }
}