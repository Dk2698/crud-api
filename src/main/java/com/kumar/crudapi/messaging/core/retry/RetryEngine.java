package com.kumar.crudapi.messaging.core.retry;

import com.kumar.crudapi.messaging.api.Message;
import com.kumar.crudapi.messaging.api.MessageHandler;
import com.kumar.crudapi.messaging.core.dlq.DeadLetterPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RetryEngine {

    private final RetryPolicy retryPolicy;
    private final RetryScheduler scheduler;
    private final DeadLetterPublisher dlq;

    public void execute(Message message,
                        MessageHandler handler,
                        String topic,
                        int attempt) {

        try {

            handler.handle(message);

        } catch (Exception ex) {

            handler.onError(message, ex);

            // ❌ If handler disables retry
            if (!handler.retryOnError()) {
                dlq.publish(topic, message, ex);
                return;
            }

            // 🔁 Retry logic
            if (retryPolicy.shouldRetry(attempt)) {

                long delay = retryPolicy.getDelay(attempt);

                scheduler.schedule(() ->
                                execute(message, handler, topic, attempt + 1),
                        delay
                );

            } else {

                dlq.publish(topic, message, ex);
            }
        }
    }
}