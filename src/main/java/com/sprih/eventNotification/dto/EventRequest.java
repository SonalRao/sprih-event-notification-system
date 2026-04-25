package com.sprih.eventNotification.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Map;

@Data
public class EventRequest {

        @NotBlank
        private String eventType;

        @NotNull
        private Map<String, Object> payload;

        @NotBlank
        private String callbackUrl;
}
