package com.smarthome.smart_home.dto.importexport;

import java.time.LocalTime;
import java.util.UUID;

import com.smarthome.smart_home.enums.automation.Action;
import com.smarthome.smart_home.enums.automation.TriggerEvent;

import lombok.Data;

@Data
public class AutomationRuleImportDTO {
    private String name;
    private String description;
    private boolean enabled;
    private TriggerEvent triggerEvent;
    private Double triggerValue;
    private LocalTime triggerTime;
    private Action action;
    private Double actionValue;
    private UUID deviceUuid;
    private UUID sensorUuid;
}
