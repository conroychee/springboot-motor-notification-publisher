package org.example.springbootwebsocketkafkamotornotification.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Data;

import java.time.Instant;

@Data
@Entity
@Table(name = "motor_alerts")
public class MotorNotification {

    public enum SensorType {
        TEMPERATURE_SENSOR,
        VIBRATION_SENSOR
    }

    public enum AlertType {
        VERY_HIGH_TEMPERATURE,
        HIGH_TEMPERATURE,
        VERY_HIGH_VIBRATION,
        HIGH_VIBRATION
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "MotorId must be valid")
    private String motorId;

    @NotNull(message = "Timestamp must be valid")
    @PastOrPresent(message = "Timestamp must be in the past or present")
    private Instant timestamp;

    @NotNull(message = "Sensor type must be valid")
    @Enumerated(EnumType.STRING)
    private SensorType sensorType;

    @Transient
    private AlertType alertType;

    @NotNull(message = "Value must be valid")
    private Double value;
}
