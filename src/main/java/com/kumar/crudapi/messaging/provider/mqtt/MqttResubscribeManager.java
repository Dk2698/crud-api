//package com.kumar.crudapi.messaging.provider.mqtt;
//
//import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
//import org.eclipse.paho.client.mqttv3.MqttCallback;
//import org.eclipse.paho.client.mqttv3.MqttClient;
//import org.eclipse.paho.client.mqttv3.MqttMessage;
//import org.springframework.stereotype.Component;
//
//import java.util.HashSet;
//import java.util.Set;
//
//@Component
//public class MqttResubscribeManager {
//
//    private final MqttClient client;
//    private final Set<String> topics = new HashSet<>();
//
//    public MqttResubscribeManager(MqttClient client) {
//        this.client = client;
//    }
//
//    public void register(String topic) {
//        topics.add(topic);
//    }
//
//    public void init() {
//
//        client.setCallback(new MqttCallback() {
//
//            @Override
//            public void connectionLost(Throwable cause) {
//                System.out.println("MQTT lost connection: " + cause.getMessage());
//                retryReconnect();
//            }
//
//            @Override
//            public void messageArrived(String topic, MqttMessage message) {}
//
//            @Override
//            public void deliveryComplete(IMqttDeliveryToken token) {}
//        });
//    }
//
//    private void retryReconnect() {
//        new Thread(() -> {
//            while (!client.isConnected()) {
//                try {
//                    Thread.sleep(3000);
//                    client.reconnect();
//
//                    for (String topic : topics) {
//                        client.subscribe(topic, 1);
//                        System.out.println("Resubscribed: " + topic);
//                    }
//
//                } catch (Exception e) {
//                    System.out.println("Reconnect failed, retrying...");
//                }
//            }
//        }).start();
//    }
//}