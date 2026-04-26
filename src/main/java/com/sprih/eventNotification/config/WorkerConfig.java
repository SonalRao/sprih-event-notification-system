package com.sprih.eventNotification.config;

import com.sprih.eventNotification.eventWorker.EventWorker;
import com.sprih.eventNotification.externalService.CallbackService;
import com.sprih.eventNotification.model.EventType;
import com.sprih.eventNotification.queue.EventQueueManager;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Configuration
public class WorkerConfig {
    private static final Logger log = LoggerFactory.getLogger(WorkerConfig.class);

    private final EventQueueManager queueManager;
    private final CallbackService callbackService;
    private ExecutorService executorService;
    private List<EventWorker> workers;

    public WorkerConfig(EventQueueManager queueManager, CallbackService callbackService) {
        this.queueManager = queueManager;
        this.callbackService = callbackService;
    }

    @PostConstruct
    public void startWorkers() {
        executorService = Executors.newFixedThreadPool(3);

        EventWorker emailWorker = new EventWorker(queueManager.getQueue(EventType.EMAIL), EventType.EMAIL, callbackService);
        EventWorker smsWorker   = new EventWorker(queueManager.getQueue(EventType.SMS),   EventType.SMS,   callbackService);
        EventWorker pushWorker  = new EventWorker(queueManager.getQueue(EventType.PUSH),  EventType.PUSH,  callbackService);

        workers = List.of(emailWorker, smsWorker, pushWorker);
        workers.forEach(executorService::submit);
        log.info("All worker threads started");
    }

    @PreDestroy
    public void shutdown() {
        log.info("Shutdown signal received...");
        queueManager.stopAcceptingEvents();
        executorService.shutdownNow();

        try {
            if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                log.warn("Workers did not finish in time — forcing shutdown");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        log.info("System Shutdown complete");
    }
}
