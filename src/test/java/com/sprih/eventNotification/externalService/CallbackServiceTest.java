package com.sprih.eventNotification.externalService;

import com.sprih.eventNotification.model.Event;
import com.sprih.eventNotification.model.EventType;
import com.sprih.eventNotification.model.StatusType;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

import static org.mockito.Mockito.*;

class CallbackServiceTest {
    @Test
    void shouldSendCallback() {
        RestTemplate restTemplate = mock(RestTemplate.class);

        CallbackService service = new CallbackService(restTemplate);

        Event event = new Event(
                "id1",
                EventType.EMAIL,
                Map.of(),
                "http://test.com",
                StatusType.COMPLETED
        );

        service.sendCallback(event);

        verify(restTemplate, times(1))
                .postForObject(eq("http://test.com"), any(), eq(String.class));
    }
}