package org.example.springbootwebsocketkafkamotornotification.component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.example.springbootwebsocketkafkamotornotification.model.MotorNotification;
import org.example.springbootwebsocketkafkamotornotification.service.RedisCacheService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(KafkaListenerWebSockerPublisher.class);
    @Autowired
    private RedisCacheService redisCacheService;

    @Autowired
    private ObjectMapper objectMapper;

    private final SimpMessagingTemplate messagingTemplate;

    @Value("${app.ws.destination}")
    private String wsDestination;


    @KafkaListener(topics = "${app.kafka.topic}")
    public void onMessage(MotorNotification motorNotification) throws JsonProcessingException {
        // Forward to all WebSocket subscribers
        logger.info("Received motor notification from kafka: {}", motorNotification);
        redisCacheService.upsert(motorNotification);

        // after inserting the data to redis, retrieves it out
        Map<String, Object> motorData = redisCacheService.findNotifications("*motor*");
        messagingTemplate.convertAndSend(wsDestination, motorData);
    }

}
