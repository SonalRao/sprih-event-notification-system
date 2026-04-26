package com.sprih.eventNotification.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
public class Event {

    private String eventId;
    private EventType eventType;
    private Map<String, Object> payload;
    private String callbackUrl;
    private StatusType status;
}
