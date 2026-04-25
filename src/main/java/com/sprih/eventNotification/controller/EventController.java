package com.sprih.eventNotification.controller;

import com.sprih.eventNotification.dto.EventRequest;
import com.sprih.eventNotification.dto.EventResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/events")
public class EventController {

    @PostMapping
    public EventResponse createEvent(@Valid @RequestBody EventRequest request) {
        String eventId = UUID.randomUUID().toString();

        System.out.println("Received event: " + request);

        return new EventResponse(eventId, "Event accepted for processing");
    }
}