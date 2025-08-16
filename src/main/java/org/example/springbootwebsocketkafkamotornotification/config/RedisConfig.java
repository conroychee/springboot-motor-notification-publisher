package org.example.springbootwebsocketkafkamotornotification.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.example.springbootwebsocketkafkamotornotification.model.MotorNotification;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.Topic;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Configuration
public class RedisConfig {

//    @Bean
//    public ObjectMapper redisObjectMapper() {
//        ObjectMapper om = new ObjectMapper();
//        om.registerModule(new JavaTimeModule()); // for Instant
//        return om;
//    }

    @Bean
    public RedisTemplate<String, MotorNotification> mNRedisTemplate(
            RedisConnectionFactory cf, ObjectMapper om) {
        RedisTemplate<String, MotorNotification> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(cf);

        StringRedisSerializer stringSer = new StringRedisSerializer();
        GenericJackson2JsonRedisSerializer jsonSer = new GenericJackson2JsonRedisSerializer(om);

        redisTemplate.setKeySerializer(stringSer);
        redisTemplate.setValueSerializer(jsonSer);
        redisTemplate.setHashKeySerializer(stringSer);
        redisTemplate.setHashValueSerializer(jsonSer);
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    @Bean
    ObjectMapper objectMapper() {
        return new ObjectMapper()
                .registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule())
                .disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }


    /*
    This is for detecting the del event in redis.
    I don't want to keep the motor that has already been fixed in the notification table
     */
    @Bean
    public Object enableKeyEventNotifications(RedisConnectionFactory factory) {
        factory.getConnection()
                .setConfig("notify-keyspace-events", "Exg"); // or "Eg" if you only want DEL
        return new Object();
    }

    @Bean
    public RedisMessageListenerContainer redisContainer(RedisConnectionFactory connectionFactory) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);

//        // Plain MessageListener (no adapter)
//        // Minimal listener: only detects that a DEL event happened
//        MessageListener listener = (message, pattern) -> {
//            System.out.println("Redis DEL event detected");
//            // You can trigger whatever logic you want here
//        };
//
//        // Only listen for delete events from DB 0
//        container.addMessageListener(
//                listener,
//                Collections.singletonList(new ChannelTopic("__keyevent@0__:del"))
//        );

        return container;
    }

}
