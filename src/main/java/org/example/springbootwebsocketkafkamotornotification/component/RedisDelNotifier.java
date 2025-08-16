package org.example.springbootwebsocketkafkamotornotification.component;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.example.springbootwebsocketkafkamotornotification.service.RedisCacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.*;


//@Component
//public class RedisDelNotifier {
//
//
//
//    @Value("${app.ws.destination}")
//    private String wsDestination;
//
//
//
//
//    public RedisDelNotifier(RedisCacheService redisCacheService,RedisMessageListenerContainer container, SimpMessagingTemplate messagingTemplate, @Value("${app.ws.destination}" String wsDestination) {
//        container.addMessageListener(
//                (message, pattern) -> {
//                    // react to any DEL
//                    System.out.println("DEL detected (from MyOtherService)");
//                },
//                java.util.Collections.singletonList(new org.springframework.data.redis.listener.ChannelTopic("__keyevent@0__:del"))
//        );
//
//        List<Map<Object, Object>> motorNotifications = redisCacheService.findNotifications("*motor*");
//
//        //List<String> machineNotificationList = redisCacheService.getNotifications();
//        messagingTemplate.convertAndSend(wsDestination, motorNotifications);
//
//    }
//}


@RequiredArgsConstructor
@Component
public class RedisDelNotifier {

    private final RedisMessageListenerContainer container;
    private final SimpMessagingTemplate messagingTemplate;
    private final RedisCacheService redisCacheService;

    @Value("${app.ws.destination}")
    private String wsDestination;

    @PostConstruct
    void subscribe() {
        container.addMessageListener(
                (message, pattern) -> {
                    // DEL happened
                    String msgStr = message.toString();


                    String recoveredMessage = msgStr.split(":")[1];
                    recoveredMessage += msgStr.contains("TEMPERATURE")? " temperature issue has been resolved" : " vibration issue has been resolved";
                    Map<String, Object> motorData = new HashMap<>();

                    System.out.println("The message has been sent: " + message);
                    var motorNotifications = redisCacheService.findNotifications("*motor*");
                    motorData.put("recoveredMsg", recoveredMessage);
                    motorData.put("motorNotifications", motorNotifications);
                    messagingTemplate.convertAndSend(wsDestination, motorData);
                },
                Collections.singletonList(new ChannelTopic("__keyevent@0__:del"))
        );
    }
}