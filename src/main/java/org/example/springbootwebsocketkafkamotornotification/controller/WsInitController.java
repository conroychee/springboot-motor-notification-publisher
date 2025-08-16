package org.example.springbootwebsocketkafkamotornotification.controller;

import org.example.springbootwebsocketkafkamotornotification.service.RedisCacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class WsInitController {
    @Autowired
    private RedisCacheService redisCacheService;


    @MessageMapping("init")
    @SendToUser("/queue/init")
    public Map<String, Object> motorNotifications(){

        List<Map<Object, Object>> motorNotifications = redisCacheService.findNotifications("*motor*");
        Map<String, Object> motorData = new HashMap<>();
        motorData.put("motorNotifications", motorNotifications);
        System.out.println("I have received here to get snapshot");
        return motorData;
    }
}
