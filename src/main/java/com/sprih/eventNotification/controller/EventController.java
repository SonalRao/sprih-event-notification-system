package com.sprih.eventNotification.controller;

import com.sprih.eventNotification.dto.EventRequest;
import com.sprih.eventNotification.dto.EventResponse;
import com.sprih.eventNotification.service.EventService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/events")
public class EventController {
    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping
    public EventResponse createEvent(@Valid @RequestBody EventRequest request) {

        String eventId = eventService.createEvent(request);

        return new EventResponse(eventId, "Event accepted for processing");
    }
}