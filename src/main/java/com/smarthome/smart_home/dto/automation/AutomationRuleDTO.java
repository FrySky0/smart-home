package com.smarthome.smart_home.dto.automation;

import com.smarthome.smart_home.enums.automation.Action;
import com.smarthome.smart_home.enums.automation.TriggerEvent;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AutomationRuleDTO {
    @NotBlank(message = "Name is required")
    private String name;
    private String description;
    private Long triggerDeviceId;
    private Long triggerSensorId;
    private TriggerEvent triggerEvent;
    private Double triggerValue;
    private Action action;

}
