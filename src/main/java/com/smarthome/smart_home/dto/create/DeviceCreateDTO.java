package com.smarthome.smart_home.dto.create;

import com.smarthome.smart_home.enums.DeviceStatus;
import com.smarthome.smart_home.enums.DeviceType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DeviceCreateDTO {
    @NotBlank(message = "Device name is required")
    private String name;

    @NotNull(message = "Device type is required")
    private DeviceType type;

    private DeviceStatus status;

    @NotNull(message = "Room ID is required")
    private Long roomId;

    private Double value;
}
