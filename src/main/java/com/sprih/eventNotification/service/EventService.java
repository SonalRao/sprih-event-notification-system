package com.sprih.eventNotification.service;

import com.sprih.eventNotification.dto.EventRequest;
import com.sprih.eventNotification.model.Event;
import com.sprih.eventNotification.model.EventType;
import com.sprih.eventNotification.queue.EventQueueManager;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
public class EventService {

    private final EventQueueManager queueManager;

    public EventService(EventQueueManager queueManager) {
        this.queueManager = queueManager;
    }

    public String createEvent(EventRequest request) {
        String eventId = UUID.randomUUID().toString();

        Event event = new Event(
                eventId,
                request.getEventType(),
                request.getPayload(),
                request.getCallbackUrl(),
                "PENDING"
        );

        queueManager.addEvent(event);
        return eventId;
    }
}
