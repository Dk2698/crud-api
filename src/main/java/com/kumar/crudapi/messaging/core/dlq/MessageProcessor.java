package com.kumar.crudapi.messaging.core.dlq;

import com.kumar.crudapi.messaging.api.Message;
import com.kumar.crudapi.messaging.api.MessageHandler;
import com.kumar.crudapi.messaging.core.retry.RetryPolicy;

//public class MessageProcessor {
//
//    private final RetryPolicy retryPolicy;
//    private final DeadLetterPublisher dlq;
//
//    public MessageProcessor(RetryPolicy retryPolicy,
//                            DeadLetterPublisher dlq) {
//        this.retryPolicy = retryPolicy;
//        this.dlq = dlq;
//    }
//
//    public void process(Message message,
//                        MessageHandler handler,
//                        int attempt) {
//
//        try {
//
//            handler.handle(message);
//
//        } catch (Exception e) {
//
//            if (retryPolicy.shouldRetry(attempt)) {
//
//                long delay = retryPolicy.getDelay(attempt);
//
//                new Thread(() -> {
//                    try {
//                        Thread.sleep(delay);
//                        process(message, handler, attempt + 1);
//                    } catch (InterruptedException ignored) {}
//                }).start();
//
//            } else {
//                dlq.publish(message.getTopic(), message, e);
//            }
//        }
//    }
//}