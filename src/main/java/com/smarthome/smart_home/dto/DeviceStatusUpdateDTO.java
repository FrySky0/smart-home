package com.smarthome.smart_home.dto;

import com.smarthome.smart_home.enums.DeviceStatus;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DeviceStatusUpdateDTO {
    @NotNull(message = "Device status is required")
    private DeviceStatus status;
}
