package com.sprih.eventNotification.eventWorker;

import com.sprih.eventNotification.model.Event;
import com.sprih.eventNotification.model.EventType;

import java.util.Random;
import java.util.concurrent.BlockingQueue;

public class EventWorker implements Runnable {

        private final BlockingQueue<Event> queue;
        private final EventType eventType;
        private volatile boolean running = true;

        public EventWorker(BlockingQueue<Event> queue, EventType eventType) {
            this.queue = queue;
            this.eventType = eventType;
        }

        @Override
        public void run() {
            while (running) {
                try {
                    Event event = queue.take();

                    System.out.println("Processing " + eventType + " event: " + event.getEventId());

                    processEvent(event);

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }

        private void processEvent(Event event) {
            try {

                switch (eventType) {
                    case EMAIL -> Thread.sleep(5000);
                    case SMS -> Thread.sleep(3000);
                    case PUSH -> Thread.sleep(2000);
                }

                if (new Random().nextInt(10) == 0) {
                    throw new RuntimeException("Simulated failure");
                }

                event.setStatus("COMPLETED");
                System.out.println("Event completed: " + event.getEventId());

            } catch (Exception e) {
                event.setStatus("FAILED");
                System.out.println("Event failed: " + event.getEventId());
            }
        }

        public void stop() {
            running = false;
        }
}
