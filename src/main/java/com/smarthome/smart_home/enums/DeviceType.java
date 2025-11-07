package com.smarthome.smart_home.enums;

public enum DeviceType {
    LIGHT(false),
    THERMOSTAT(false),
    AIR_CONDITIONER(true),
    TV(false);

    private final boolean hasValue;
    DeviceType(boolean hasValue) {
        this.hasValue = hasValue;
    }
    public boolean hasValue() {
        return hasValue;
    }
}
