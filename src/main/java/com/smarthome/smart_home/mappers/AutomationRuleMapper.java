package com.smarthome.smart_home.mappers;

import org.springframework.stereotype.Component;

import com.smarthome.smart_home.dto.AutomationRuleValidatable;
import com.smarthome.smart_home.dto.response.AutomationRuleResponseDTO;
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

    public AutomationRule toEntity(AutomationRuleValidatable dto, Device device, Sensor sensor) {
        AutomationRule rule = new AutomationRule();
        rule.setName(dto.getName());
        rule.setDescription(dto.getDescription());
        rule.setEnabled(dto.getEnabled());
        rule.setDevice(device);
        rule.setSensor(sensor);
        rule.setTriggerEvent(dto.getTriggerEvent());
        rule.setTriggerValue(dto.getTriggerValue());
        rule.setTriggerTime(dto.getTriggerTime());
        rule.setAction(dto.getAction());
        rule.setActionValue(dto.getActionValue());
        return rule;
    }

    public AutomationRuleResponseDTO toResponseDTO(AutomationRule rule) {
        AutomationRuleResponseDTO dto = new AutomationRuleResponseDTO();
        dto.setId(rule.getId());
        dto.setName(rule.getName());
        dto.setDescription(rule.getDescription());
        dto.setTriggerDevice(deviceMapper.toResponseDTO(rule.getDevice()));
        if (rule.getSensor() != null){
            dto.setTriggerSensor(sensorMapper.toDTO(rule.getSensor()));
        }else{
            dto.setTriggerSensor(null);
        }
        dto.setEnabled(rule.isEnabled());
        dto.setTriggerEvent(rule.getTriggerEvent());
        dto.setTriggerValue(rule.getTriggerValue());
        dto.setTriggerTime(rule.getTriggerTime());
        dto.setAction(rule.getAction());
        dto.setActionValue(rule.getActionValue());
        return dto;
    }

}
