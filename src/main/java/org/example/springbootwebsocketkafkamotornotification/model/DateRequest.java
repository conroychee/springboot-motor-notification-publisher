package org.example.springbootwebsocketkafkamotornotification.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.Instant;


@Data
public class DateRequest {
    private Instant startDateTime;
    private Instant endDateTime;

}
