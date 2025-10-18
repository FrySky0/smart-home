package com.smarthome.smart_home.dto;

import com.smarthome.smart_home.enums.SensorType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateSensorDTO {
    @NotBlank(message = "Sensor name is required")
    private String name;

    @NotNull(message = "Sensor type is required")
    private SensorType type;

    @NotNull(message = "Room ID is required")
    private Long roomId;
}
