package com.smarthome.smart_home.dto.automation;

import com.smarthome.smart_home.dto.DeviceDTO;
import com.smarthome.smart_home.dto.SensorDTO;
import lombok.Data;

@Data
public class AutomationRuleResponseDTO {
    private Long id;
    private String name;
    private String description;
    private DeviceDTO triggerDevice;
    private SensorDTO triggerSensor;
    private boolean enabled;
    private String triggerEvent;
    private Integer triggerValue;
    private String action;
}
