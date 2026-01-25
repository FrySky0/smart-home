package com.smarthome.smart_home.dto.update.patch;

import com.smarthome.smart_home.enums.DeviceStatus;

import lombok.Data;

@Data
public class DevicePatchDTO {
    private String name;
    private DeviceStatus status;
    private Long roomId;
    private Double value;
}
