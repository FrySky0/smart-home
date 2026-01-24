package com.smarthome.smart_home.dto;

import java.util.UUID;

import com.smarthome.smart_home.enums.SensorType;

import lombok.Data;

@Data
public class SensorDTO {
    private Long id;
    private String name;
    private SensorType type;
    private Double value;
    private Long roomId; // возвращаем только id комнаты, вместо объекта
    private UUID uuid;

    public Long getRoomId() {
        return roomId;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }
}
