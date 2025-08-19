package org.example.springbootwebsocketkafkamotornotification.model;

import lombok.Data;

import java.time.Instant;
import java.util.List;


@Data
public class MotorCountRequest {
    private Instant startDateTime;
    private Instant endDateTime;
    List<String> motorList;
}
