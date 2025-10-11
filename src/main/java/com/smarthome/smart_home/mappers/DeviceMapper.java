package com.smarthome.smart_home.mappers;

import org.springframework.stereotype.Component;

import com.smarthome.smart_home.dto.DeviceDTO;
import com.smarthome.smart_home.model.Device;

@Component
public class DeviceMapper {
    // Преобразование сущности Device в DTO
    public DeviceDTO toDTO(Device device) {
        DeviceDTO dto = new DeviceDTO();
        dto.setId(device.getId());
        dto.setName(device.getName());
        dto.setType(device.getType());
        dto.setStatus(device.getStatus());
        dto.setRoomId(device.getRoom().getId());
        return dto;
    }
}
