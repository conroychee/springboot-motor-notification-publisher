package org.example.springbootwebsocketkafkamotornotification.component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.example.springbootwebsocketkafkamotornotification.model.MotorNotification;
import org.example.springbootwebsocketkafkamotornotification.service.RedisCacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class KafkaListenerWebSockerPublisher {

    @Autowired
    private RedisCacheService redisCacheService;

    @Autowired
    private ObjectMapper objectMapper;

    private final SimpMessagingTemplate messagingTemplate;

    @Value("${app.ws.destination}")
    private String wsDestination;

    // Consumes raw String; adjust if you need JSON->POJO
    @KafkaListener(topics = "${app.kafka.topic}")
    public void onMessage(MotorNotification motorNotification) throws JsonProcessingException {
        // Forward to all WebSocket subscribers
        System.out.println("Received message: " + motorNotification);
        redisCacheService.upsert(motorNotification);
        List<Map<Object, Object>> motorNotifications = redisCacheService.findNotifications("*motor*");
        Map<String, Object> motorData = new HashMap<>();
        motorData.put("motorNotifications", motorNotifications);
        //List<String> machineNotificationList = redisCacheService.getNotifications();
        messagingTemplate.convertAndSend(wsDestination, motorData);
    }

}
