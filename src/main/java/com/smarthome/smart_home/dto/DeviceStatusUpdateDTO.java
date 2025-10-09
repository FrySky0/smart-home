package com.smarthome.smart_home.dto;

import com.smarthome.smart_home.model.Device;

import lombok.Data;

@Data
public class DeviceStatusUpdateDTO {
    private Device.DeviceStatus status;
}
