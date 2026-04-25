package com.sprih.eventNotification.config;

import com.sprih.eventNotification.eventWorker.EventWorker;
import com.sprih.eventNotification.externalService.CallbackService;
import com.sprih.eventNotification.model.EventType;
import com.sprih.eventNotification.queue.EventQueueManager;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Configuration
public class WorkerConfig {

    private final EventQueueManager queueManager;
    private final CallbackService callbackService;
    private ExecutorService executorService;

    public WorkerConfig(EventQueueManager queueManager, CallbackService callbackService) {
        this.queueManager = queueManager;
        this.callbackService = callbackService;
    }

    @PostConstruct
    public void startWorkers() {
        executorService = Executors.newFixedThreadPool(3);

        executorService.submit(
                new EventWorker(queueManager.getQueue(EventType.EMAIL), EventType.EMAIL, callbackService)
        );

        executorService.submit(
                new EventWorker(queueManager.getQueue(EventType.SMS), EventType.SMS, callbackService)
        );

        executorService.submit(
                new EventWorker(queueManager.getQueue(EventType.PUSH), EventType.PUSH, callbackService)
        );
        System.out.println("Worker threads started...");
    }

    @PreDestroy
    public void shutdown() {
        System.out.println("Shutting down gracefully...");

        executorService.shutdown();

        try {
            if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
        }

        System.out.println("Shutdown complete.");
    }
}
