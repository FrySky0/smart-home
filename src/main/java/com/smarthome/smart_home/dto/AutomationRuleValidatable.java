package com.smarthome.smart_home.dto;

import java.time.LocalTime;
import java.util.UUID;

import com.smarthome.smart_home.enums.automation.Action;
import com.smarthome.smart_home.enums.automation.TriggerEvent;

public interface AutomationRuleValidatable {
    String getName();
    String getDescription();
    Boolean getEnabled();
    TriggerEvent getTriggerEvent();
    LocalTime getTriggerTime();
    UUID getSensorUuid();
    Double getTriggerValue();
    Action getAction();
    Double getActionValue();
    UUID getDeviceUuid();
}