package org.example.springbootwebsocketkafkamotornotification.service;

import org.example.springbootwebsocketkafkamotornotification.model.MotorNotification;
import org.example.springbootwebsocketkafkamotornotification.repository.MotorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class MotorNotificationService {

    @Autowired
    public  MotorRepository motorRepository;

    public List<MotorNotification> getMotorNotificationsByRange(Instant start, Instant end){
        return motorRepository.findMotorNotificationsByTimestampBetween(start, end);
    }

}
