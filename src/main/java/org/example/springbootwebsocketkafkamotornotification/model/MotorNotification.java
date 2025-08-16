package org.example.springbootwebsocketkafkamotornotification.model;

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
//
//    @NotNull(message = "Vibration must be valid")
//    @DecimalMin(value = "0.0", inclusive = true, message = "Vibration must be >= 0.0")
//    private Double vibration;

//    @NotNull(message = "Temperature must be valid")
//    @DecimalMin(value = "-50.0", inclusive = true, message = "Temperature must be >= -50.0")
//    private Double temperature;

    @NotNull(message = "Sensor type must be valid")
    @Enumerated(EnumType.STRING)
    private SensorType sensorType;

    @Transient
    private AlertType alertType;

    @NotNull(message = "Value must be valid")
    private Double value;
}
