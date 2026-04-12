package com.kumar.crudapi.messaging.serializer;

import com.kumar.crudapi.messaging.api.MessageHandler;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class HandlerTypeResolver {

    public Class<?> resolve(Object handler) {

        for (Type iface : handler.getClass().getGenericInterfaces()) {

            if (iface instanceof ParameterizedType pt) {

                if (pt.getRawType().equals(MessageHandler.class)) {
                    return (Class<?>) pt.getActualTypeArguments()[0];
                }
            }
        }

        throw new RuntimeException("Cannot resolve handler type: " + handler.getClass());
    }
}