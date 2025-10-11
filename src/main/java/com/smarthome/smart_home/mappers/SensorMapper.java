package com.smarthome.smart_home.mappers;

import org.springframework.stereotype.Component;

import com.smarthome.smart_home.dto.SensorDTO;
import com.smarthome.smart_home.model.Sensor;

@Component
public class SensorMapper {
    public SensorDTO toDTO(Sensor sensor) {
        SensorDTO dto = new SensorDTO();
        dto.setId(sensor.getId());
        dto.setName(sensor.getName());
        dto.setType(sensor.getType());
        dto.setValue(sensor.getValue());
        dto.setRoomId(sensor.getRoom().getId());
        return dto;
    }
}
