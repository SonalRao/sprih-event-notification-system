package com.sprih.eventNotification.service;

import com.sprih.eventNotification.dto.EventRequest;
import com.sprih.eventNotification.model.Event;
import com.sprih.eventNotification.model.EventType;
import com.sprih.eventNotification.queue.EventQueueManager;
import org.springframework.stereotype.Service;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class EventService {

    private final EventQueueManager queueManager;
    private final AtomicInteger counter = new AtomicInteger(1);

    public EventService(EventQueueManager queueManager) {
        this.queueManager = queueManager;
    }

    public String createEvent(EventRequest request) {
        String eventId = "e" + counter.getAndIncrement();

        Event event = new Event(
                eventId,
                EventType.valueOf(request.getEventType().toUpperCase()),
                request.getPayload(),
                request.getCallbackUrl(),
                "PENDING"
        );

        queueManager.addEvent(event);

        return eventId;
    }
}
