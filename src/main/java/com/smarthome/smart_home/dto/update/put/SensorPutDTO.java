package com.smarthome.smart_home.dto.update.put;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SensorPutDTO {
    @NotBlank(message = "Sensor name is required")
    private String name;
    @NotNull(message = "Sensor value is required")
    private Double value;
    @NotNull(message = "Room ID is required")
    private Long roomId;
}
