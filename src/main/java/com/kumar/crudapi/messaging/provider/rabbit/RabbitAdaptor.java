//package com.kumar.crudapi.messaging.provider.rabbit;
//
//import com.kumar.crudapi.messaging.api.PubSubClient;
//import lombok.extern.slf4j.Slf4j;
//
//@Slf4j
//public class RabbitAdapter implements PubSubClient {
//
//    private final RabbitTemplate rabbitTemplate;
//    private final SimpleRabbitListenerContainerFactory factory;
//    private final SubscriptionRegistry registry;
//
//    private final Map<String, SimpleMessageListenerContainer> containers =
//            new ConcurrentHashMap<>();
//
//    public RabbitAdapter(RabbitTemplate rabbitTemplate,
//                         SimpleRabbitListenerContainerFactory factory,
//                         SubscriptionRegistry registry) {
//
//        this.rabbitTemplate = rabbitTemplate;
//        this.factory = factory;
//        this.registry = registry;
//    }
//
//    // ================= PUBLISH =================
//
//    @Override
//    public void publish(Message message) {
//        publish(message, new PublishOptions());
//    }
//
//    @Override
//    public void publish(Message message, PublishOptions options) {
//
//        try {
//
//            String exchange = options.getExchange() != null
//                    ? options.getExchange()
//                    : "";
//
//            String routingKey = message.getTopic();
//
//            rabbitTemplate.convertAndSend(
//                    exchange,
//                    routingKey,
//                    message.getPayload().toString()
//            );
//
//        } catch (Exception e) {
//            throw new RuntimeException("RabbitMQ publish failed", e);
//        }
//    }
//
//    @Override
//    public CompletableFuture<Void> publishAsync(Message message) {
//
//        return CompletableFuture.runAsync(() -> publish(message));
//    }
//
//    // ================= SUBSCRIBE =================
//
//    @Override
//    public void subscribe(String queue, MessageHandler handler) {
//        subscribe(queue, handler, new SubscribeOptions());
//    }
//
//    @Override
//    public void subscribe(String queue,
//                          MessageHandler handler,
//                          SubscribeOptions options) {
//
//        try {
//
//            registry.register(queue, handler);
//
//            // 🔥 Create listener container per queue
//            SimpleMessageListenerContainer container =
//                    factory.createListenerContainer();
//
//            container.setQueueNames(queue);
//
//            container.setMessageListener((MessageListener) msg -> {
//
//                String payload = new String(msg.getBody());
//
//                Message message = new Message(queue, payload);
//
//                CompletableFuture.runAsync(() -> {
//                    try {
//                        handler.handle(message);
//                    } catch (Exception e) {
//                        handler.onError(message, e);
//                        log.error("RabbitMQ message failed: {}", queue, e);
//                    }
//                });
//            });
//
//            container.start();
//
//            containers.put(queue, container);
//
//            log.info("RabbitMQ subscribed to queue: {}", queue);
//
//        } catch (Exception e) {
//            throw new RuntimeException("RabbitMQ subscribe failed", e);
//        }
//    }
//
//    @Override
//    public void unsubscribe(String queue) {
//
//        SimpleMessageListenerContainer container = containers.remove(queue);
//
//        if (container != null) {
//            container.stop();
//        }
//
//        registry.remove(queue);
//
//        log.info("RabbitMQ unsubscribed from: {}", queue);
//    }
//
//    // ================= CLOSE =================
//
//    @Override
//    public void close() {
//
//        containers.values().forEach(container -> {
//            try {
//                container.stop();
//            } catch (Exception e) {
//                log.error("Error stopping container", e);
//            }
//        });
//
//        log.info("RabbitMQ adapter closed");
//    }
//}
