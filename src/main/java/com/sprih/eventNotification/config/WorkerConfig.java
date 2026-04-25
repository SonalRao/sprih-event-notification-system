package com.sprih.eventNotification.config;

import com.sprih.eventNotification.eventWorker.EventWorker;
import com.sprih.eventNotification.externalService.CallbackService;
import com.sprih.eventNotification.model.EventType;
import com.sprih.eventNotification.queue.EventQueueManager;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class WorkerConfig {

    private final EventQueueManager queueManager;
    private final CallbackService callbackService;

    public WorkerConfig(EventQueueManager queueManager, CallbackService callbackService) {
        this.queueManager = queueManager;
        this.callbackService = callbackService;
    }

    @PostConstruct
    public void startWorkers() {
        Thread emailWorker = new Thread(
                new EventWorker(queueManager.getQueue(EventType.EMAIL), EventType.EMAIL, callbackService)
        );

        Thread smsWorker = new Thread(
                new EventWorker(queueManager.getQueue(EventType.SMS), EventType.SMS, callbackService)
        );

        Thread pushWorker = new Thread(
                new EventWorker(queueManager.getQueue(EventType.PUSH), EventType.PUSH, callbackService)
        );

        emailWorker.start();
        smsWorker.start();
        pushWorker.start();
    }
}
