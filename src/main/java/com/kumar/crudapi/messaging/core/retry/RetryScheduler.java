package com.kumar.crudapi.messaging.core.retry;

import com.kumar.crudapi.messaging.api.Message;
import com.kumar.crudapi.messaging.api.PubSubClient;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
public class RetryScheduler {

    private final ScheduledExecutorService executor =
            Executors.newScheduledThreadPool(4);

    public void schedule(Runnable task, long delayMs) {
        executor.schedule(task, delayMs, TimeUnit.MILLISECONDS);
    }
}