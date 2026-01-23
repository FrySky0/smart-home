package com.smarthome.smart_home.dto.importexport;

import java.util.List;

import lombok.Data;

@Data
public class RoomImportDTO {
    private String name;
    private Integer floor;
    private List<DeviceImportDTO> devices;
    private List<SensorImportDTO> sensors;
}
