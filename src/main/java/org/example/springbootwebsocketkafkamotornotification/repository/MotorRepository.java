package org.example.springbootwebsocketkafkamotornotification.repository;

import org.example.springbootwebsocketkafkamotornotification.model.DailyCount;
import org.example.springbootwebsocketkafkamotornotification.model.MotorNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface MotorRepository extends JpaRepository<MotorNotification, Long> {
    List<MotorNotification> findMotorNotificationsByTimestampBetween(Instant start, Instant end);


    //get the motor daily count within time range
    @Query(value = """
        SELECT CAST("timestamp" AS date) AS date,
               motor_id                  AS motorId,
               COUNT(*)                  AS count
        FROM motor_alerts
        WHERE "timestamp" >= :startDateTime
          AND "timestamp" <= :endDateTime
        GROUP BY CAST("timestamp" AS date), motor_id
        """,
            nativeQuery = true)
    List<DailyCount> findDailyCountByTimestampBetween(@Param("startDateTime") Instant startDateTime, @Param("endDateTime") Instant endDateTime);

    //get the motor daily count within time range
    @Query(value = """
        SELECT CAST("timestamp" AS date) AS date,
               motor_id                  AS motorId,
               COUNT(*)                  AS count
        FROM motor_alerts
        WHERE "timestamp" >= :startDateTime
          AND "timestamp" <= :endDateTime
          AND motor_id IN :motor_list
        GROUP BY CAST("timestamp" AS date), motor_id
        """,
            nativeQuery = true)
    List<DailyCount> findDailyCountByMotorListAndTimestampBetween(@Param("startDateTime") Instant startDateTime, @Param("endDateTime") Instant endDateTime, @Param("motor_list") List<String> motorList);



}
