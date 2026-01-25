package com.smarthome.smart_home.dto.response;

import java.time.LocalTime;

import com.smarthome.smart_home.enums.automation.Action;
import com.smarthome.smart_home.enums.automation.TriggerEvent;

import lombok.Data;

@Data
public class AutomationRuleResponseDTO {
    private Long id;
    private String name;
    private String description;
    private Boolean enabled;
    private DeviceResponseDTO triggerDevice;
    private SensorResponseDTO triggerSensor;
    private TriggerEvent triggerEvent;
    private Double triggerValue;
    private LocalTime triggerTime;
    private Action action;
    private Double actionValue;
}
