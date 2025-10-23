package com.smarthome.smart_home.dto.automation;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AutomationRuleDTO {
    @NotBlank(message = "Name is required")
    private String name;
    private String description;
    private Long triggerDeviceId;
    private Long triggerSensorId;
    private String triggerEvent;
    private Integer triggerValue;
    private String action;

}
