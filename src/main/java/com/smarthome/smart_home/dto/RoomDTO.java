package com.smarthome.smart_home.dto;

import java.util.List;

import lombok.Data;

@Data
public class RoomDTO {
    private Long id;
    private String name;
    private Integer floor;
    private List<DeviceDTO> devices;
    private List<SensorDTO> sensors;
}
