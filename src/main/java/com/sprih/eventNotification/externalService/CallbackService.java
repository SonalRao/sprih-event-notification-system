package com.sprih.eventNotification.externalService;

import com.sprih.eventNotification.model.Event;
import com.sprih.eventNotification.model.StatusType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Service
public class CallbackService {
    private static final Logger log = LoggerFactory.getLogger(CallbackService.class);
    private final RestTemplate restTemplate;

    public CallbackService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

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
            log.error("Callback failed for event [{}]: {}", event.getEventId(), e.getMessage());
        }
    }
}
