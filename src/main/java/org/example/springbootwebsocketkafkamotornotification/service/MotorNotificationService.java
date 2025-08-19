package org.example.springbootwebsocketkafkamotornotification.service;

import lombok.RequiredArgsConstructor;
import org.example.springbootwebsocketkafkamotornotification.model.DailyCount;
import org.example.springbootwebsocketkafkamotornotification.model.MotorCountRequest;
import org.example.springbootwebsocketkafkamotornotification.model.MotorNotification;
import org.example.springbootwebsocketkafkamotornotification.repository.MotorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MotorNotificationService {

    private static final Logger logger = LoggerFactory.getLogger(MotorNotificationService.class);

    public final  MotorRepository motorRepository;

    public List<MotorNotification> getMotorNotificationsByRange(Instant start, Instant end){
        return motorRepository.findMotorNotificationsByTimestampBetween(start, end);
    }

    /**
     * Get the daily count
     * @param motorReqMap
     * @return Daily count map
     */
    public Map<String, Object> getDailyCount(MotorCountRequest motorReqMap) {
        logger.info("start date: {}", motorReqMap.getStartDateTime());
        //logger.info("The found daily count: {}", motorRepository.findDailyCountByTimestampBetween(motorReqMap.getStartDateTime(), motorReqMap.getEndDateTime()));
        List<DailyCount> dailyCountList = new ArrayList<>();
        if(motorReqMap.getMotorList().isEmpty()) {
            dailyCountList = motorRepository.findDailyCountByTimestampBetween(motorReqMap.getStartDateTime(), motorReqMap.getEndDateTime());
        }
        else{
            dailyCountList = motorRepository.findDailyCountByMotorListAndTimestampBetween(motorReqMap.getStartDateTime(), motorReqMap.getEndDateTime(), motorReqMap.getMotorList());
        }
        List<String> motorList = dailyCountList.stream()
                .map(DailyCount::getMotorId)
                .distinct()
                .sorted(Comparator.comparingInt(id -> Integer.parseInt(id.split("-")[1])))
                .toList();

        List<Date> dateList = dailyCountList.stream().map(DailyCount::getDate).distinct().sorted().toList();

        Map<String, Map<String, Long>> motorCountMap = new HashMap<>();
        dailyCountList.forEach(dc ->
                motorCountMap
                        .computeIfAbsent(dc.getDate().toString(), k -> new HashMap<>())
                        .put(dc.getMotorId(), dc.getCount())
        );
        List<Map<String, Object>> series = new ArrayList<>();

        for (String motorId : motorList) {
            Map<String, Object> map = new HashMap<>();
            map.put("name", motorId);
            List<Long> data = new LinkedList<>();
            for (Date date : dateList) {
                String dateStr = date.toString();
                Long count = motorCountMap.getOrDefault(dateStr, Map.of())
                        .getOrDefault(motorId, 0L);
                data.add(count);
            }
            map.put("data", data);
            series.add(map);
        }
        logger.info("dates: {}", dateList);
        logger.info("series: {}", series);

        Map<String, Object> result = new HashMap<>();
        result.put("categories", dateList);
        result.put("series", series);

        logger.info("Daily count {}", result);
        return result;
    }

    /*
    Deprecated function
    put here for reference
     */
    public Map<String, Object> getCountDeprecated(MotorCountRequest motorReqMap) {
        List<MotorNotification> motorNotifications = getMotorNotificationsByRange(motorReqMap.getStartDateTime(), motorReqMap.getEndDateTime());
        logger.debug("motorNotifications: {}" , motorNotifications);
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
        logger.debug("Motor ids: {}", motorIds);

        List<LocalDate> dates = motorNotifications.stream().map(x -> (LocalDate)
                x.getTimestamp().atZone(ZoneId.of("UTC")).toLocalDate()).distinct().sorted().toList();


        List<Map<String, Object>> series = new ArrayList<>();


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
        logger.info("dates: {}", dates);
        logger.info("series: {}", series);

        Map<String, Object>  result = new HashMap<>();
        result.put("categories", dates);
        result.put("series", series);

        logger.info("Daily count {}", result);
        return result;
    }



}
