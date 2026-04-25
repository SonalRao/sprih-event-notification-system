package com.sprih.eventNotification.config;

import com.sprih.eventNotification.eventWorker.EventWorker;
import com.sprih.eventNotification.model.EventType;
import com.sprih.eventNotification.queue.EventQueueManager;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WorkerConfig {

    private final EventQueueManager queueManager;

    public WorkerConfig(EventQueueManager queueManager) {
        this.queueManager = queueManager;
    }

    @PostConstruct
    public void startWorkers() {

        Thread emailWorker = new Thread(
                new EventWorker(queueManager.getQueue(EventType.EMAIL), EventType.EMAIL)
        );

        Thread smsWorker = new Thread(
                new EventWorker(queueManager.getQueue(EventType.SMS), EventType.SMS)
        );

        Thread pushWorker = new Thread(
                new EventWorker(queueManager.getQueue(EventType.PUSH), EventType.PUSH)
        );

        emailWorker.start();
        smsWorker.start();
        pushWorker.start();
    }
}
