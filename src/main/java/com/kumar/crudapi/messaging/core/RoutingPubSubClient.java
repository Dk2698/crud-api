//package com.kumar.crudapi.messaging.core;
//
//import com.kumar.crudapi.messaging.api.*;
//import com.kumar.crudapi.messaging.config.TopicConfig;
//import com.kumar.crudapi.messaging.config.TopicConfiguration;
//import com.kumar.crudapi.messaging.provider.kafka.KafkaAdapter;
//import com.kumar.crudapi.messaging.provider.mqtt.MqttAdapter;
//import org.springframework.stereotype.Component;
//
//import java.util.concurrent.CompletableFuture;
//
//@Component
//public class RoutingPubSubClient implements PubSubClient {
//
//    private final TopicConfiguration config;
//
//    private final MqttAdapter mqttAdapter;
//    private final KafkaAdapter kafkaAdapter;
////    private final GcpPubSubAdapter gcpAdapter;
//
//    public RoutingPubSubClient(TopicConfiguration config,
//                               MqttAdapter mqttAdapter,
//                               KafkaAdapter kafkaAdapter) {
//        this.config = config;
//        this.mqttAdapter = mqttAdapter;
//        this.kafkaAdapter = kafkaAdapter;
//    }
//
//    public void publish(Object message, String topic) {
//
//        TopicConfig topicConfig = config.getTopic(topic);
//        Message message1 = new Message();
//        message1.setTopic(topic);
//        message1.setTimestamp(System.currentTimeMillis());
//        message1.setPayload(message);
//
//        switch (topicConfig.getProvider()) {
//            case "mqtt":
//                mqttAdapter.publish(message1);
//                break;
//
//            case "kafka":
//                kafkaAdapter.publish(message1);
//                break;
//
////            case "gcp":
////                gcpAdapter.publish(topicConfig.getTopicId(), message);
////                break;
//
//            default:
//                throw new RuntimeException("Unsupported provider: " + topicConfig.getProvider());
//        }
//    }
//
//    @Override
//    public void publish(Message message) {
//
//    }
//
//    @Override
//    public CompletableFuture<Void> publishAsync(Message message) {
//        return null;
//    }
//
//    @Override
//    public void publish(Message message, PublishOptions options) {
//
//    }
//
//    @Override
//    public void subscribe(String topic, MessageHandler handler) {
//
//        TopicConfig topicConfig = config.getTopic(topic);
//
//        switch (topicConfig.getProvider()) {
//            case "mqtt":
//                mqttAdapter.subscribe(topicConfig.getTopicId(), handler);
//                break;
//
//            case "kafka":
//                kafkaAdapter.subscribe(topicConfig.getTopicId(), handler);
//                break;
//
//            case "gcp":
//                throw new UnsupportedOperationException("Use subscription-based API for GCP");
//        }
//    }
//
//    @Override
//    public void subscribe(String topic, MessageHandler handler, SubscribeOptions options) {
//
//    }
//
//    @Override
//    public void unsubscribe(String topic) {
//
//    }
//
//    @Override
//    public void close() {
//
//    }
//}