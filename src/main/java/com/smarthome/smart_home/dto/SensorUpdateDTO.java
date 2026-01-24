package com.smarthome.smart_home.dto;

import lombok.Data;

@Data
public class SensorUpdateDTO {
    private String name;
    private Double value;
    private Long roomId;
}
