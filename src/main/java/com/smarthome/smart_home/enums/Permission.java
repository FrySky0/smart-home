package com.smarthome.smart_home.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Permission {
    ROOM_READ("room:read"),
    ROOM_CONTROL("room:control"),
    ROOM_MANAGE("room:manage"),

    DEVICE_READ("device:read"),
    DEVICE_CONTROL("device:control"), 
    DEVICE_MANAGE("device:manage"), 

    SENSOR_READ("sensor:read"),
    SENSOR_CONTROL("sensor:control"), 
    SENSOR_MANAGE("sensor:manage"), 

    AUTOMATIONRULE_READ("automationrule:read"),
    AUTOMATIONRULE_CONTROL("automationrule:control"), 
    AUTOMATIONRULE_MANAGE("automationrule:manage"), 

    SYSTEMREPORT_DOWNLOAD("systemreport:download"),

    CONFIGURATION_EXPORT("configuration:export"),
    CONFIGURATION_IMPORT("configuration:import"),

    USERS_MANAGE("users:manage");

    @Getter
    private final String permission;
}