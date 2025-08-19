package org.example.springbootwebsocketkafkamotornotification.model;


import lombok.Data;
import lombok.Value;

import java.sql.Date;
import java.time.LocalDate;

@Value
public class DailyCount {
    Date date;
    String motorId;
    long count;
}