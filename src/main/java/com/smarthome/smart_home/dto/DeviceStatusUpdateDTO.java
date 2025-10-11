package com.smarthome.smart_home.dto;


import com.smarthome.smart_home.enums.DeviceStatus;

import lombok.Data;

@Data
public class DeviceStatusUpdateDTO {
    private DeviceStatus status;
}
