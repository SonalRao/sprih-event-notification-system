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
    private static final int MAX_RETRIES = 3;
    private static final long BASE_BACKOFF_MS = 1000L;
    private final BlockingQueue<Event> queue;
    private final EventType eventType;
    private final CallbackService callbackService;
    private final Random random;

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
                processWithRetry(event);

            } catch (InterruptedException e) {
                break;
            }
        }

        log.info("{} worker interrupted — draining {} remaining item(s)", eventType, queue.size());
        Event remaining;
        while ((remaining = queue.poll()) != null) {
            processWithRetry(remaining);
        }
        log.info("{} worker stopped cleanly", eventType);
    }

    private void processWithRetry(Event event) {
        int attempt = 0;
        while (attempt < MAX_RETRIES) {
            attempt++;
            try {
                log.info("Processing {} event [{}] — attempt {}/{}", eventType, event.getEventId(),  attempt, MAX_RETRIES);
                Thread.sleep(getProcessingDelayMs(eventType));

                if (random.nextInt(10) == 0) {
                    throw new RuntimeException("Simulated failure");
                }

                event.setStatus(StatusType.COMPLETED);
                log.info("Event completed [{}] on attempt {}", event.getEventId(), attempt);
                callbackService.sendCallback(event);
                return;

            }catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                event.setStatus(StatusType.COMPLETED);
                log.info("Event completed during shutdown [{}]", event.getEventId());
                callbackService.sendCallback(event);

            } catch (Exception e) {
                log.warn("Attempt {}/{} failed for event [{}]: {}",
                        attempt, MAX_RETRIES, event.getEventId(), e.getMessage());

                if (attempt < MAX_RETRIES) {
                    long backoffMs = BASE_BACKOFF_MS * (long) Math.pow(2, attempt - 1);
                    log.info("Retrying event [{}] in {}ms...", event.getEventId(), backoffMs);
                    try {
                        Thread.sleep(backoffMs);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        event.setStatus(StatusType.FAILED);
                        callbackService.sendCallback(event);
                        return;
                    }
                } else {
                    event.setStatus(StatusType.FAILED);
                    log.error("Event permanently failed after {} attempts [{}]",
                            MAX_RETRIES, event.getEventId());
                    callbackService.sendCallback(event);
                }
            }
        }
    }

    private long getProcessingDelayMs(EventType type) {
        return switch (type) {
            case EMAIL -> 5000L;
            case SMS   -> 3000L;
            case PUSH  -> 2000L;
        };
    }
}