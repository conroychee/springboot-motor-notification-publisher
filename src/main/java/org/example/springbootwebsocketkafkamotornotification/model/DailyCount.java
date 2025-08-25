package org.example.springbootwebsocketkafkamotornotification.model;


import lombok.Value;
import java.sql.Date;


@Value
public class DailyCount {
    Date date;
    String motorId;
    long count;
}