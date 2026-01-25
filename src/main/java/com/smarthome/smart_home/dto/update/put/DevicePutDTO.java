package com.smarthome.smart_home.dto.update.put;


import com.smarthome.smart_home.enums.DeviceStatus;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DevicePutDTO {
    @NotBlank(message = "Device name is required")
    private String name;
    @NotNull(message = "Device status is required")
    private DeviceStatus status;
    @NotNull(message = "Room ID is required")
    private Long roomId;
    private Double value;
}
