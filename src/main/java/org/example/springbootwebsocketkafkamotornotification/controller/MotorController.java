package org.example.springbootwebsocketkafkamotornotification.controller;

import org.example.springbootwebsocketkafkamotornotification.model.MotorNotification;
import org.example.springbootwebsocketkafkamotornotification.repository.MotorRepository;
import org.example.springbootwebsocketkafkamotornotification.service.MotorNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class MotorController {

    //@Autowired
    private final MotorNotificationService motorNotificationService;

    public MotorController(MotorNotificationService motorNotificationService) {
        this.motorNotificationService = motorNotificationService;
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @PostMapping("/dailyCount")
    public Map<String, Object> findDailyCount(@RequestBody Map<String, String> dateMap) {

        String startDateTime = dateMap.get("startDateTime");
        String endDateTime = dateMap.get("endDateTime");
        Instant start = Instant.parse(startDateTime);
        Instant end = Instant.parse(endDateTime);

        List<MotorNotification> motorNotifications = motorNotificationService.getMotorNotificationsByRange(start, end);
        System.out.println("motorNotifications: " + motorNotifications);

        Map<String, Map<String, Integer>> motorCountMap = new HashMap<>();
        for (MotorNotification motorNotification : motorNotifications) {
            String dateKey = motorNotification.getTimestamp()
                    .atZone(ZoneId.of("UTC"))
                    .toLocalDate()
                    .toString();

            motorCountMap
                    .computeIfAbsent(dateKey, k -> new HashMap<>())
                    .merge(motorNotification.getMotorId(), 1, Integer::sum);
        }

        List<String> motorIds = motorNotifications.stream()
                .map(MotorNotification::getMotorId)
                .distinct()
                .sorted(Comparator.comparingInt(id -> Integer.parseInt(id.split("-")[1])))
                .collect(Collectors.toList());
        System.out.println(motorIds);

        List<LocalDate> dates = motorNotifications.stream().map(x -> (LocalDate)
                x.getTimestamp().atZone(ZoneId.of("UTC")).toLocalDate()).distinct().sorted().toList();


        List<Map<String, Object>> series = new ArrayList<>();
        System.out.println(motorCountMap);
        System.out.println(dates);


        for (String motorId : motorIds) {
            Map<String, Object> map = new HashMap<>();
            map.put("name", motorId);
            List<Integer> data = new LinkedList<>();
            for (LocalDate date : dates) {
                String dateStr = date.toString();
                int count = motorCountMap.getOrDefault(dateStr, Map.of())
                        .getOrDefault(motorId, 0);
                data.add(count);
            }
            map.put("data", data);
            series.add(map);
        }

        System.out.println("series: " + series);
        //return motorNotificationService.getMotorNotificationsByRange(start, end);


        Map<String, Object>  result = new HashMap<>();

        result.put("categories", dates);
        result.put("series", series);

        return result;

    }

}
