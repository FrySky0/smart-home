package com.smarthome.smart_home.dto;


import com.smarthome.smart_home.enums.DeviceStatus;

import lombok.Data;

@Data
public class DeviceUpdateDTO {
    private String name;
    private DeviceStatus status;
    private Long roomId; // возвращаем только id комнаты, вместо объекта
    private Double value;
}
