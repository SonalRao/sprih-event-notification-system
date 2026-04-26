package com.sprih.eventNotification.service;

import com.sprih.eventNotification.dto.EventRequest;
import com.sprih.eventNotification.model.Event;
import com.sprih.eventNotification.model.EventType;
import com.sprih.eventNotification.model.StatusType;
import com.sprih.eventNotification.queue.EventQueueManager;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;
import java.util.UUID;

@Service
public class EventService {

    private final EventQueueManager queueManager;

    public EventService(EventQueueManager queueManager) {
        this.queueManager = queueManager;
    }

    public String createEvent(EventRequest request) {
        if (!queueManager.isAcceptingEvents()) {
            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "System is shutting down, not accepting new events"
            );
        }
        validatePayload(request.getEventType(), request.getPayload());
        String eventId = UUID.randomUUID().toString();

        Event event = new Event(
                eventId,
                request.getEventType(),
                request.getPayload(),
                request.getCallbackUrl(),
                StatusType.PENDING
        );

        queueManager.addEvent(event);
        return eventId;
    }

    private void validatePayload(EventType type, Map<String, Object> payload) {
        switch (type) {
            case EMAIL -> requireFields(payload, "recipient", "message");
            case SMS   -> requireFields(payload, "phoneNumber", "message");
            case PUSH  -> requireFields(payload, "deviceId", "message");
        }
    }

    private void requireFields(Map<String, Object> payload, String... fields) {
        for (String field : fields) {
            if (payload == null || !payload.containsKey(field) || payload.get(field) == null) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Missing required payload field: " + field
                );
            }
        }
    }
}
