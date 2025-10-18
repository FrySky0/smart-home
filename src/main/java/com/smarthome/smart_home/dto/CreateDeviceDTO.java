package com.smarthome.smart_home.dto;

import com.smarthome.smart_home.enums.DeviceType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateDeviceDTO {
    @NotBlank(message = "Device name is required")
    private String name;

    @NotNull(message = "Device type is required")
    private DeviceType type;

    @NotNull(message = "Room ID is required")
    private Long roomId;
}
