package org.example.springbootwebsocketkafkamotornotification.component;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.example.springbootwebsocketkafkamotornotification.service.RedisCacheService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.*;

@RequiredArgsConstructor
@Component
public class RedisDelNotifier {

    private static final Logger logger = LoggerFactory.getLogger(RedisDelNotifier.class);
    private final RedisMessageListenerContainer container;
    private final SimpMessagingTemplate messagingTemplate;
    private final RedisCacheService redisCacheService;

    @Value("${app.ws.destination}")
    private String wsDestination;

    /**
     * When there is deletion on redis, the recovered machine data needs to be delivered to dashboard
     */
    @PostConstruct
    void subscribe() {
        container.addMessageListener(
                (message, pattern) -> {
                    // DEL happened
                    String msgStr = message.toString();
                    String recoveredMessage = msgStr.split(":")[1];
                    recoveredMessage += msgStr.contains("TEMPERATURE")? " temperature issue has been resolved" : " vibration issue has been resolved";
                    Map<String, Object> motorData = redisCacheService.findNotifications("*motor*");
                    motorData.put("recoveredMsg", recoveredMessage);

                    messagingTemplate.convertAndSend(wsDestination, motorData);
                    logger.info("Sending the recovered message " + recoveredMessage);
                },
                Collections.singletonList(new ChannelTopic("__keyevent@0__:del"))
        );
    }
}