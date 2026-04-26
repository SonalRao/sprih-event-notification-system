package com.sprih.eventNotification.eventWorker;

import com.sprih.eventNotification.externalService.CallbackService;
import com.sprih.eventNotification.model.Event;
import com.sprih.eventNotification.model.EventType;
import com.sprih.eventNotification.model.StatusType;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static org.mockito.Mockito.*;

class EventWorkerTest {
    @Test
    void shouldDrainQueueAndProcessEventsOnInterruption() throws InterruptedException {

        BlockingQueue<Event> queue = new LinkedBlockingQueue<>();

        CallbackService callbackService = mock(CallbackService.class);

        Random random = mock(Random.class);
        when(random.nextInt(10)).thenReturn(1);

        EventWorker worker = new EventWorker(queue, EventType.PUSH, callbackService, random);

        Event e1 = new Event("1", EventType.PUSH, Map.of(), "url", StatusType.PENDING);
        Event e2 = new Event("2", EventType.PUSH, Map.of(), "url", StatusType.PENDING);

        queue.add(e1);
        queue.add(e2);

        Thread thread = new Thread(worker);

        thread.start();

        Thread.sleep(200);

        thread.interrupt();

        thread.join();

        verify(callbackService, times(2)).sendCallback(any(Event.class));
    }

}