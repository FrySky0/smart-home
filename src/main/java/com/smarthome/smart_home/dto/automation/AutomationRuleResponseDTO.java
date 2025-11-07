package com.smarthome.smart_home.dto.automation;

import com.smarthome.smart_home.dto.DeviceDTO;
import com.smarthome.smart_home.dto.SensorDTO;
import com.smarthome.smart_home.enums.automation.Action;
import com.smarthome.smart_home.enums.automation.TriggerEvent;

import lombok.Data;

@Data
public class AutomationRuleResponseDTO {
    private Long id;
    private String name;
    private String description;
    private DeviceDTO triggerDevice;
    private SensorDTO triggerSensor;
    private boolean enabled;
    private TriggerEvent triggerEvent;
    private Double triggerValue;
    private Action action;
    private Double actionValue;
}
