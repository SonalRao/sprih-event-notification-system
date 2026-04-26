package com.sprih.eventNotification.service;

import com.sprih.eventNotification.dto.EventRequest;
import com.sprih.eventNotification.model.EventType;
import com.sprih.eventNotification.queue.EventQueueManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class EventServiceTest {
    private EventQueueManager queueManager;
    private EventService eventService;

    @BeforeEach
    void setup() {
        queueManager = mock(EventQueueManager.class);
        when(queueManager.isAcceptingEvents()).thenReturn(true);
        eventService = new EventService(queueManager);
    }

    @Test
    void shouldCreateEventSuccessfully() {
        EventRequest request = new EventRequest();
        request.setEventType(EventType.EMAIL);

        Map<String, Object> payload = new HashMap<>();
        payload.put("recipient", "test@example.com");
        payload.put("message", "Hello");

        request.setPayload(payload);
        request.setCallbackUrl("http://test.com");

        String eventId = eventService.createEvent(request);

        assertNotNull(eventId);
        verify(queueManager, times(1)).addEvent(any());
    }

    @Test
    void shouldThrowExceptionWhenSystemShuttingDown() {
        when(queueManager.isAcceptingEvents()).thenReturn(false);

        EventRequest request = new EventRequest();
        request.setEventType(EventType.EMAIL);
        request.setPayload(Map.of("recipient", "a", "message", "b"));
        request.setCallbackUrl("url");

        assertThrows(ResponseStatusException.class, () ->
                eventService.createEvent(request)
        );
    }

    @Test
    void shouldFailWhenPayloadMissingFields() {
        EventRequest request = new EventRequest();
        request.setEventType(EventType.EMAIL);
        request.setPayload(Map.of("recipient", "only"));
        request.setCallbackUrl("url");

        assertThrows(ResponseStatusException.class, () ->
                eventService.createEvent(request)
        );
    }
}