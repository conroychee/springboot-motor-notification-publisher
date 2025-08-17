package org.example.springbootwebsocketkafkamotornotification.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.springbootwebsocketkafkamotornotification.model.MotorNotification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.hash.Jackson2HashMapper;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

@Service
public class RedisCacheService {

    private static final Logger log = LoggerFactory.getLogger(RedisCacheService.class);
    private final Jackson2HashMapper mapper = new Jackson2HashMapper(false); // no flatten
    private final RedisTemplate<String, MotorNotification> redisTemplate;

    @Autowired
    private ObjectMapper om;

    public RedisCacheService(RedisTemplate<String, MotorNotification> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void upsert(MotorNotification m) {
        log.info("Hash value serialize: {}", redisTemplate.getHashValueSerializer().getClass());

        String key = m.getSensorType().toString() + "-motor:" + m.getMotorId();
        Map<String, Object> map = new HashMap<>();
        map.put("motorId", m.getMotorId());
        map.put("timestamp", m.getTimestamp());
        map.put("alertType", m.getAlertType().toString());
        map.put("sensorType", m.getSensorType().toString());
        map.put("value", m.getValue());
        log.info("The motor data to be upserted {}", map);
        redisTemplate.opsForHash().putAll(key, map);
    }

    /**
     * Find the motor notification in redis that has a certain key pattern
     * @param keyPattern
     * @return List of motor notifiications
     */
    public Map<String, Object> findNotifications(String keyPattern) {
        Map<String, Object> motorData = new HashMap<>();
        List<Map<Object, Object>> motorNotifications = new ArrayList<>();

        ScanOptions options = ScanOptions.scanOptions().match(keyPattern).build();
        try (Cursor<String> cursor = redisTemplate.scan(options)) {
            while (cursor.hasNext()) {
                String key = cursor.next();
                // Check if the key is a hash type before retrieving its content
                if (redisTemplate.type(key) == org.springframework.data.redis.connection.DataType.HASH) {
                    Map<Object, Object> hashEntries = redisTemplate.opsForHash().entries(key);
                    Instant timestamp = Instant.parse(hashEntries.get("timestamp").toString());
                    hashEntries.put("timestamp", timestamp);
                    motorNotifications.add(hashEntries);
                }
            }
        } catch (Exception e) {
            // Handle exceptions
            e.printStackTrace();
        }

        motorNotifications.sort(Comparator.comparing((Map<Object, Object> m) -> (Instant) m.get("timestamp"),
                        Comparator.nullsLast(Comparator.naturalOrder()))
                .reversed());

        motorData.put("motorNotifications", motorNotifications);
        log.info("motor data {}", motorData);
        return motorData;
    }

}
