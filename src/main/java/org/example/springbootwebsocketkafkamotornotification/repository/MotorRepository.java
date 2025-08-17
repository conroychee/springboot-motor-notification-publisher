package org.example.springbootwebsocketkafkamotornotification.repository;

import org.example.springbootwebsocketkafkamotornotification.model.MotorNotification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;

public interface MotorRepository extends JpaRepository<MotorNotification, Long> {
    List<MotorNotification> findMotorNotificationsByTimestampBetween(Instant start, Instant end);
}
