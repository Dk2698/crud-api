package com.kumar.crudapi.messaging.core.registry;

import com.kumar.crudapi.messaging.api.MessageHandler;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SubscriptionRegistry {

    private final Map<String, MessageHandler> handlers = new ConcurrentHashMap<>();

    public void register(String topic, MessageHandler handler) {
        handlers.put(topic, handler);
    }

    public void remove(String topic) {
        handlers.remove(topic);
    }

    public MessageHandler get(String topic) {
        return handlers.get(topic);
    }

    public Map<String, MessageHandler> getAll() {
        return handlers;
    }
}
