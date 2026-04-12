package com.kumar.crudapi.messaging.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface MqttSubscriber {
    String topicKey();
    String group() default "";   // future Kafka-like grouping
    Class<?> type();
}