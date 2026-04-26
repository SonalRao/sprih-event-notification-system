package com.sprih.eventNotification.eventWorker;

import com.sprih.eventNotification.externalService.CallbackService;
import com.sprih.eventNotification.model.Event;
import com.sprih.eventNotification.model.EventType;
import com.sprih.eventNotification.model.StatusType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Random;
import java.util.concurrent.BlockingQueue;

public class EventWorker implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(EventWorker.class);
    private final BlockingQueue<Event> queue;
    private final EventType eventType;
    private final CallbackService callbackService;
    private final Random random;
    private volatile boolean running = true;

    public EventWorker(BlockingQueue<Event> queue, EventType eventType, CallbackService callbackService) {
        this(queue, eventType, callbackService, new Random());
    }

    public EventWorker(BlockingQueue<Event> queue, EventType eventType, CallbackService callbackService, Random random) {
        this.queue = queue;
        this.eventType = eventType;
        this.callbackService = callbackService;
        this.random = random;
    }

    @Override
    public void run() {
        log.info("{} worker started", eventType);
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Event event = queue.take();
                processEvent(event);

            } catch (InterruptedException e) {
                break;
            }
        }

        log.info("{} worker interrupted — draining {} remaining item(s)", eventType, queue.size());
        Event remaining;
        while ((remaining = queue.poll()) != null) {
            processEvent(remaining);
        }
        log.info("{} worker stopped cleanly", eventType);
    }

    private void processEvent(Event event) {
        try {
            log.info("Processing {} event [{}]", eventType, event.getEventId());
            switch (eventType) {
                case EMAIL -> Thread.sleep(5000);
                case SMS -> Thread.sleep(3000);
                case PUSH -> Thread.sleep(2000);
            }

            if (random.nextInt(10) == 0) {
                throw new RuntimeException("Simulated failure");
            }

            event.setStatus(StatusType.COMPLETED);
            log.info("Event completed [{}]", event.getEventId());
            callbackService.sendCallback(event);

        }catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            event.setStatus(StatusType.COMPLETED);
            log.info("Event completed during shutdown [{}]", event.getEventId());
            callbackService.sendCallback(event);

        } catch (Exception e) {
            event.setStatus(StatusType.FAILED);
            log.warn("Event failed [{}]: {}", event.getEventId(), e.getMessage());
            callbackService.sendCallback(event);
        }
    }
}
