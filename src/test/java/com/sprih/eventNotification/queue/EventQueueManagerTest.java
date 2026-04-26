package com.sprih.eventNotification.queue;

import com.sprih.eventNotification.model.Event;
import com.sprih.eventNotification.model.EventType;
import com.sprih.eventNotification.model.StatusType;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class EventQueueManagerTest {
    @Test
    void shouldRouteEventToCorrectQueue() {
        EventQueueManager manager = new EventQueueManager();

        Event event = new Event("id1", EventType.EMAIL, Map.of(), "url", StatusType.PENDING);

        manager.addEvent(event);

        assertEquals(1, manager.getQueue(EventType.EMAIL).size());
        assertEquals(0, manager.getQueue(EventType.SMS).size());
    }

    @Test
    void shouldStopAcceptingEvents() {
        EventQueueManager manager = new EventQueueManager();

        manager.stopAcceptingEvents();

        assertFalse(manager.isAcceptingEvents());
    }

}