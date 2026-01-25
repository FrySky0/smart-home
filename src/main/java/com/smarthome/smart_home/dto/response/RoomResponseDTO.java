package com.smarthome.smart_home.dto.response;

import java.util.List;

import lombok.Data;

@Data
public class RoomResponseDTO {
    private Long id;
    private String name;
    private Integer floor;
    private List<DeviceResponseDTO> devices;
    private List<SensorResponseDTO> sensors;
}
