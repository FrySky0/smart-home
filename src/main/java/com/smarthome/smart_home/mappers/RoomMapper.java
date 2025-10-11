package com.smarthome.smart_home.mappers;

import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.smarthome.smart_home.dto.CreateRoomDTO;
import com.smarthome.smart_home.dto.RoomDTO;
import com.smarthome.smart_home.model.Room;

@Component
public class RoomMapper {
    private final DeviceMapper deviceMapper;
    private final SensorMapper sensorMapper;

    public RoomMapper(DeviceMapper deviceMapper, SensorMapper sensorMapper) {
        this.deviceMapper = deviceMapper;
        this.sensorMapper = sensorMapper;
    }

    // Преобразование сущности Room в DTO
    public RoomDTO toDTO(Room room) {
        RoomDTO dto = new RoomDTO();
        dto.setId(room.getId());
        dto.setName(room.getName());
        dto.setFloor(room.getFloor());

        if (room.getDevices() != null) {
            dto.setDevices(
                    room.getDevices().stream().map(deviceMapper::toDTO).collect(Collectors.toList()));
        }

        if (room.getSensors() != null) {
            dto.setSensors(
                    room.getSensors().stream().map(sensorMapper::toDTO).collect(Collectors.toList()));
        }
        return dto;
    }

    // Преобразование DTO в сущность Room
    public Room toEntity(CreateRoomDTO dto) {
        Room room = new Room();
        room.setName(dto.getName());
        room.setFloor(dto.getFloor());
        return room;
    }
}
