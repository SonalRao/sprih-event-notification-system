package com.sprih.eventNotification.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Event {

    private String eventId;
    private EventType eventType;
    private Object payload;
    private String callbackUrl;
    private StatusType status;
}
