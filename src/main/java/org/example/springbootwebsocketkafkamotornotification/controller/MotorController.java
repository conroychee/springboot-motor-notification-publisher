package org.example.springbootwebsocketkafkamotornotification.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.example.springbootwebsocketkafkamotornotification.model.MotorCountRequest;
import org.example.springbootwebsocketkafkamotornotification.service.MotorNotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.*;

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
    public Map<String, Object> findDailyCount(@RequestBody MotorCountRequest motorReqMap) {
        logger.info("Received the request to get the daily count of motor notifications");
        logger.info("motorReqMap: {}", motorReqMap);

        if(motorReqMap.getMotorList().isEmpty()){
            return motorNotificationService.getDailyCount(motorReqMap);
        }
        return motorNotificationService.getDailyCount(motorReqMap);





    }




}
