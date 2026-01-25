package com.smarthome.smart_home.dto.update.patch;

import lombok.Data;

@Data
public class SensorPatchDTO {
    private String name;
    private Double value;
    private Long roomId;
}
