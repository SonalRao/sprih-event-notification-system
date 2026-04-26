package com.sprih.eventNotification.queue;

import com.sprih.eventNotification.model.Event;
import com.sprih.eventNotification.model.EventType;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Component
public class EventQueueManager {
    private final BlockingQueue<Event> emailQueue = new LinkedBlockingQueue<>();
    private final BlockingQueue<Event> smsQueue = new LinkedBlockingQueue<>();
    private final BlockingQueue<Event> pushQueue = new LinkedBlockingQueue<>();

    @Getter
    private volatile boolean acceptingEvents = true;

    public void stopAcceptingEvents() {
        acceptingEvents = false;
    }

    public void addEvent(Event event) {
        switch (event.getEventType()) {
            case EMAIL -> emailQueue.add(event);
            case SMS -> smsQueue.add(event);
            case PUSH -> pushQueue.add(event);
        }
    }

    public BlockingQueue<Event> getQueue(EventType type) {
        return switch (type) {
            case EMAIL -> emailQueue;
            case SMS -> smsQueue;
            case PUSH -> pushQueue;
        };
    }
}
