package com.sprih.eventNotification.dto;

import com.sprih.eventNotification.model.EventType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Map;

@Data
public class EventRequest {

        @NotNull
        private EventType eventType;

        @NotNull
        private Map<String, Object> payload;

        @NotBlank
        private String callbackUrl;
}
