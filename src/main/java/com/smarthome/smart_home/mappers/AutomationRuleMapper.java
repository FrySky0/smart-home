package com.smarthome.smart_home.mappers;

import org.springframework.stereotype.Component;

import com.smarthome.smart_home.dto.automation.AutomationRuleDTO;
import com.smarthome.smart_home.dto.automation.AutomationRuleResponseDTO;
import com.smarthome.smart_home.model.AutomationRule;
import com.smarthome.smart_home.model.Device;
import com.smarthome.smart_home.model.Sensor;

@Component
public class AutomationRuleMapper {
    private final DeviceMapper deviceMapper;
    private final SensorMapper sensorMapper;

    public AutomationRuleMapper(DeviceMapper deviceMapper, SensorMapper sensorMapper) {
        this.deviceMapper = deviceMapper;
        this.sensorMapper = sensorMapper;
    }

    public AutomationRule toEntity(AutomationRuleDTO dto, Device device, Sensor sensor) {
        AutomationRule rule = new AutomationRule();
        rule.setName(dto.getName());
        rule.setDescription(dto.getDescription());
        rule.setTriggerDevice(device);
        rule.setTriggerSensor(sensor);
        rule.setTriggerEvent(dto.getTriggerEvent());
        rule.setTriggerValue(dto.getTriggerValue());
        rule.setAction(dto.getAction());
        rule.setActionValue(dto.getActionValue());
        return rule;
    }

    public AutomationRuleResponseDTO toResponseDTO(AutomationRule rule) {
        AutomationRuleResponseDTO dto = new AutomationRuleResponseDTO();
        dto.setId(rule.getId());
        dto.setName(rule.getName());
        dto.setDescription(rule.getDescription());
        dto.setTriggerDevice(deviceMapper.toDTO(rule.getTriggerDevice()));
        dto.setTriggerSensor(sensorMapper.toDTO(rule.getTriggerSensor()));
        dto.setEnabled(rule.isEnabled());
        dto.setTriggerEvent(rule.getTriggerEvent());
        dto.setTriggerValue(rule.getTriggerValue());
        dto.setAction(rule.getAction());
        dto.setActionValue(rule.getActionValue());
        return dto;
    }

    public AutomationRuleDTO toDTO(AutomationRule rule) {
        AutomationRuleDTO dto = new AutomationRuleDTO();
        dto.setName(rule.getName());
        dto.setDescription(rule.getDescription());
        dto.setTriggerDeviceId(rule.getTriggerDevice().getId());
        dto.setTriggerSensorId(rule.getTriggerSensor().getId());
        dto.setTriggerEvent(rule.getTriggerEvent());
        dto.setTriggerValue(rule.getTriggerValue());
        dto.setAction(rule.getAction());
        dto.setActionValue(rule.getActionValue());
        return dto;
    }

}
