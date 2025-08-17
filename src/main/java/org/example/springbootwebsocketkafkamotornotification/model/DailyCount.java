package org.example.springbootwebsocketkafkamotornotification.model;


import lombok.Data;

import java.sql.Date;
import java.time.LocalDate;

public class DailyCount {
    private final Date date;
    private final String motorId;
    private final long count;

    public DailyCount(Date date, String motorId, long count) {  // single 3-arg ctor
        this.date = date;
        this.motorId = motorId;
        this.count = count;
    }

    public Date getDate() { return date; }
    public String getMotorId() { return motorId; }
    public long getCount() { return count; }
}