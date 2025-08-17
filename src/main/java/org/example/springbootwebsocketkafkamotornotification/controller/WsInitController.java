package org.example.springbootwebsocketkafkamotornotification.controller;

import lombok.RequiredArgsConstructor;
import org.example.springbootwebsocketkafkamotornotification.service.RedisCacheService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

import java.util.Map;

@RequiredArgsConstructor
@Controller
public class WsInitController {

    private static final Logger log = LoggerFactory.getLogger(WsInitController.class);

    private final RedisCacheService redisCacheService;

    @MessageMapping("init")
    @SendToUser("/queue/init")
    public Map<String, Object> motorNotifications(){
        log.info("Received the request to retrieve the motor notifications snapshot");
        return redisCacheService.findNotifications("*motor*");
    }
}
