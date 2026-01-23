package com.smarthome.smart_home.dto.automation;

import java.util.UUID;

import com.smarthome.smart_home.enums.automation.Action;
import com.smarthome.smart_home.enums.automation.TriggerEvent;

import lombok.Data;

@Data
public class AutomationRuleDTO {
    private String name;
    private String description;
    private UUID triggerDeviceUuid;
    private UUID triggerSensorUuid;
    private TriggerEvent triggerEvent;
    private Double triggerValue;
    private Action action;
    private Double actionValue;

}
