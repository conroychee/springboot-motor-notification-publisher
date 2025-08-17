package org.example.springbootwebsocketkafkamotornotification.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.example.springbootwebsocketkafkamotornotification.model.DailyCount;
import org.example.springbootwebsocketkafkamotornotification.model.DateRequest;
import org.example.springbootwebsocketkafkamotornotification.model.MotorNotification;
import org.example.springbootwebsocketkafkamotornotification.repository.MotorRepository;
import org.example.springbootwebsocketkafkamotornotification.service.MotorNotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class MotorController {

    private static final Logger logger = LoggerFactory.getLogger(MotorController.class);

    private final MotorNotificationService motorNotificationService;

    @Operation(summary = "Request the motor alert daily count",
            description = "Receive start date time and end date time for querying from postgres"
            )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Found"),
            @ApiResponse(responseCode = "404", description = "Not found")
    })
    @CrossOrigin(origins = "http://localhost:3000")
    @PostMapping("/dailyCount")
    public Map<String, Object> findDailyCount(@RequestBody DateRequest dateMap) {
        logger.info("Received the request to get the daily count of motor notifications");
        logger.info("dateMap: {}", dateMap);

        return motorNotificationService.getDailyCount(dateMap);

//        Map<String, Map<String, Integer>> motorCountMap = new HashMap<>();
//        for (MotorNotification motorNotification : motorNotifications) {
//            String dateKey = motorNotification.getTimestamp()
//                    .atZone(ZoneId.of("UTC"))
//                    .toLocalDate()
//                    .toString();
//
//            motorCountMap
//                    .computeIfAbsent(dateKey, k -> new HashMap<>())
//                    .merge(motorNotification.getMotorId(), 1, Integer::sum);
//        }
//
//        List<String> motorIds = motorNotifications.stream()
//                .map(MotorNotification::getMotorId)
//                .distinct()
//                .sorted(Comparator.comparingInt(id -> Integer.parseInt(id.split("-")[1])))
//                .collect(Collectors.toList());
//        logger.debug("Motor ids: {}", motorIds);
//
//        List<LocalDate> dates = motorNotifications.stream().map(x -> (LocalDate)
//                x.getTimestamp().atZone(ZoneId.of("UTC")).toLocalDate()).distinct().sorted().toList();
//
//
//        List<Map<String, Object>> series = new ArrayList<>();
//
//
//        for (String motorId : motorIds) {
//            Map<String, Object> map = new HashMap<>();
//            map.put("name", motorId);
//            List<Integer> data = new LinkedList<>();
//            for (LocalDate date : dates) {
//                String dateStr = date.toString();
//                int count = motorCountMap.getOrDefault(dateStr, Map.of())
//                        .getOrDefault(motorId, 0);
//                data.add(count);
//            }
//            map.put("data", data);
//            series.add(map);
//        }
//        logger.info("dates: {}", dates);
//        logger.info("series: {}", series);
//
//        Map<String, Object>  result = new HashMap<>();
//        result.put("categories", dates);
//        result.put("series", series);
//
//        logger.info("Daily count {}", result);
//        return result;

    }




}
