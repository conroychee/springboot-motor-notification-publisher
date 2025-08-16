package org.example.springbootwebsocketkafkamotornotification.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.springbootwebsocketkafkamotornotification.model.MotorNotification;
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

    private final Jackson2HashMapper mapper = new Jackson2HashMapper(false); // no flatten
    private final RedisTemplate<String, MotorNotification> redisTemplate;

    @Autowired
    private ObjectMapper om;

    public RedisCacheService(RedisTemplate<String, MotorNotification> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void upsert(MotorNotification m) {
        System.out.println("hash value ser: {}" + redisTemplate.getHashValueSerializer().getClass());

        String key = m.getSensorType().toString() + "-motor:" + m.getMotorId();
        Map<String, Object> map = new HashMap<>();
        map.put("motorId", m.getMotorId());
        map.put("timestamp", m.getTimestamp());            // Instant serialized as JSON
        map.put("alertType", m.getAlertType().toString());            // enum/String ok
        map.put("sensorType", m.getSensorType().toString());
        map.put("value", m.getValue());                    // Double ok

        redisTemplate.opsForHash().putAll(key, map);

    }

    public List<Map<Object, Object>> findNotifications(String keyPattern) {

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
                    System.out.println(hashEntries);
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


        System.out.println("result" + motorNotifications);
        return motorNotifications;
    }

//    public List<String> getNotifications(){
//        Set<String> keys = redisTemplate.opsForSet().members("idx:machine:MTR");
//        List<String> items = redisTemplate.opsForValue().multiGet(keys);
//        System.out.println(items);
//        //return items;
//        items.stream().forEach(x->{
//            MachineNotification machineNotification = om.convertValue(x, MachineNotification.class);
//        });
//        return items;
//    }

//    public List<MachineNotification> findByPrefix(String prefix) {
//        String pattern = prefix.endsWith("*") ? prefix : prefix + "*";
//
//        List<String> keys = new ArrayList<>();
//        ScanOptions scanOptions = ScanOptions.scanOptions().match(pattern).count(1000).build();
//
//        try (Cursor<byte[]> cursor = redisTemplate.execute((RedisCallback<Cursor<byte[]>>) conn ->
//                conn.keyCommands().scan(scanOptions))) {
//            if (cursor != null) {
//                while (cursor.hasNext()) {
//                    keys.add(new String(cursor.next(), java.nio.charset.StandardCharsets.UTF_8));
//                }
//            }
//        } catch (IOException e) {
//            throw new UncheckedIOException(e);
//        }
//
//        if (keys.isEmpty()) return List.of();
//        List<Machine> items = redisTemplate.opsForValue().multiGet(keys);
//        return items == null ? List.of() : items.stream().filter(Objects::nonNull).toList();
//    }

}
