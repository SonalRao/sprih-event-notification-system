package com.sprih.eventNotification.externalService;

import com.sprih.eventNotification.model.Event;
import com.sprih.eventNotification.model.StatusType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Service
public class CallbackService {

    private final RestTemplate restTemplate = new RestTemplate();

    public void sendCallback(Event event) {

        try {
            Map<String, Object> body = new HashMap<>();
            body.put("eventId", event.getEventId());
            body.put("status", event.getStatus());
            body.put("eventType", event.getEventType().toString());
            body.put("processedAt", Instant.now().toString());

            if (StatusType.FAILED.equals(event.getStatus())) {
                body.put("errorMessage", "Simulated processing failure");
            }

            restTemplate.postForObject(event.getCallbackUrl(), body, String.class);

        } catch (Exception e) {
            System.out.println("Callback failed for event: " + event.getEventId());
        }
    }
}
