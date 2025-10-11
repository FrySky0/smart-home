package com.smarthome.smart_home.dto;

import com.smarthome.smart_home.enums.DeviceStatus;
import com.smarthome.smart_home.enums.DeviceType;

import lombok.Data;

@Data
public class DeviceDTO {
    private Long id;
    private String name;
    private DeviceType type;
    private DeviceStatus status;
    private Long roomid; // возвращаем только id комнаты, вместо объекта

    public Long getRoomId() {
        return roomid;
    }

    public void setRoomId(Long roomid) {
        this.roomid = roomid;
    }
}
