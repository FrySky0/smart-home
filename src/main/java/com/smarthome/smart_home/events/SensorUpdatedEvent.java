package com.smarthome.smart_home.events;

import com.smarthome.smart_home.model.Sensor;

public class SensorUpdatedEvent {
    private final Sensor sensor;

    public SensorUpdatedEvent(Sensor sensor) {
        this.sensor = sensor;
    }

    public Sensor getSensor() {
        return sensor;
    }
}
