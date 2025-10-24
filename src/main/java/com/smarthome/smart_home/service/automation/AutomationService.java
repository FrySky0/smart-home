package com.smarthome.smart_home.service.automation;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import com.smarthome.smart_home.model.Sensor;
import com.smarthome.smart_home.repository.AutomationRepository;
import com.smarthome.smart_home.repository.SensorRepository;
import com.smarthome.smart_home.service.DeviceService;
import com.smarthome.smart_home.service.SensorService;

import lombok.RequiredArgsConstructor;

import com.smarthome.smart_home.dto.automation.AutomationRuleDTO;
import com.smarthome.smart_home.dto.automation.AutomationRuleResponseDTO;
import com.smarthome.smart_home.enums.automation.Action;
import com.smarthome.smart_home.enums.automation.TriggerEvent;
import com.smarthome.smart_home.events.SensorUpdatedEvent;
import com.smarthome.smart_home.mappers.AutomationRuleMapper;
import com.smarthome.smart_home.model.AutomationRule;
import com.smarthome.smart_home.model.Device;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class AutomationService {
    // private final SensorService sensorService;
    private final DeviceService deviceService;
    private final SensorService sensorService;
    private final AutomationRepository automationRepository;
    private final SensorRepository sensorRepository;
    private final AutomationRuleMapper automationRuleMapper;

    public List<AutomationRule> getAllRules() {
        return automationRepository.findAll();
    }

    public List<AutomationRule> getRulesByFilters(
            Boolean enabled,
            TriggerEvent triggerEvent,
            Integer triggerValue,
            Long triggerDeviceId,
            Long triggerSensorId,
            Action action) {
        return automationRepository.findByFilters(enabled, triggerEvent, triggerValue, triggerDeviceId, triggerSensorId,
                action);
    }

    // Затриггерить все правила
    public List<AutomationRule> triggerAll() {
        List<AutomationRule> rules = automationRepository.findByEnabled(true);
        List<AutomationRule> actionedRules = new ArrayList<>();
        for (AutomationRule rule : rules) {
            if (checkCondition(rule, rule.getTriggerSensor())) {
                goAction(rule);
                actionedRules.add(rule);
            }
        }
        return actionedRules;
    }

    public AutomationRuleResponseDTO createRule(AutomationRuleDTO dto) {
        System.out.println("Creating automation rule: " + dto);
        Device device = deviceService.getDeviceById(dto.getTriggerDeviceId());
        Sensor sensor = sensorService.getSensorById(dto.getTriggerSensorId());
        AutomationRule rule = automationRuleMapper.toEntity(dto, device, sensor);
        automationRepository.save(rule);
        return automationRuleMapper.toResponseDTO(rule);
    }

    public AutomationRuleResponseDTO updateRule(Long id, AutomationRuleDTO dto) {
        AutomationRule existingRule = automationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Automation rule not found with id: " + id));
        Device device = deviceService.getDeviceById(dto.getTriggerDeviceId());
        Sensor sensor = sensorService.getSensorById(dto.getTriggerSensorId());
        existingRule.setName(dto.getName());
        existingRule.setDescription(dto.getDescription());
        existingRule.setTriggerDevice(device);
        existingRule.setTriggerSensor(sensor);
        existingRule.setTriggerEvent(dto.getTriggerEvent());
        existingRule.setTriggerValue(dto.getTriggerValue());
        existingRule.setAction(dto.getAction());
        automationRepository.save(existingRule);
        return automationRuleMapper.toResponseDTO(existingRule);
    }

    public AutomationRuleResponseDTO partiallyUpdateRule(Long id, AutomationRuleDTO dto) {
        AutomationRule existingRule = automationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Automation rule not found with id: " + id));
        if (dto.getName() != null) {
            existingRule.setName(dto.getName());
        }
        if (dto.getDescription() != null) {
            existingRule.setDescription(dto.getDescription());
        }
        if (dto.getTriggerDeviceId() != null) {
            Device device = deviceService.getDeviceById(dto.getTriggerDeviceId());
            existingRule.setTriggerDevice(device);
        }
        if (dto.getTriggerSensorId() != null) {
            Sensor sensor = sensorService.getSensorById(dto.getTriggerSensorId());
            existingRule.setTriggerSensor(sensor);
        }
        if (dto.getTriggerEvent() != null) {
            existingRule.setTriggerEvent(dto.getTriggerEvent());
        }
        if (dto.getTriggerValue() != null) {
            existingRule.setTriggerValue(dto.getTriggerValue());
        }
        if (dto.getAction() != null) {
            existingRule.setAction(dto.getAction());
        }
        automationRepository.save(existingRule);
        return automationRuleMapper.toResponseDTO(existingRule);
    }

    public void deleteRule(Long id) {
        automationRepository.deleteById(id);
    }

    // Триггер сенсора
    // public void sensorTrigger(Sensor sensor) {
    // List<AutomationRule> rules =
    // automationRepository.findByEnabledAndTriggerSensorId(true, sensor.getId());
    // // List<AutomationRule> actionedRules = new ArrayList<>();
    // for (AutomationRule rule : rules) {
    // if (checkCondition(rule, sensor)) {
    // goAction(rule);
    // // actionedRules.add(rule);
    // }
    // }
    // // return actionedRules;

    // }

    @EventListener
    public void sensorTrigger(SensorUpdatedEvent event) {
        Sensor sensor = event.getSensor();
        List<AutomationRule> rules = automationRepository.findByEnabledAndTriggerSensorId(true, sensor.getId());
        for (AutomationRule rule : rules) {
            if (checkCondition(rule, sensor)) {
                goAction(rule);
            }
        }
    }

    private boolean checkCondition(AutomationRule rule, Sensor sensor) {
        switch (rule.getTriggerEvent()) {
            case GREATER_THAN:
                return sensor.getValue() > rule.getTriggerValue();
            case LESS_THAN:
                return sensor.getValue() < rule.getTriggerValue();
            case EQUALS:
                return sensor.getValue().equals(rule.getTriggerValue());
            default:
                return false;
        }
    }

    private void goAction(AutomationRule rule) {
        switch (rule.getAction()) {
            case TURN_ON:
                deviceService.turnOn(rule.getTriggerDevice());
                break;
            case TURN_OFF:
                deviceService.turnOff(rule.getTriggerDevice());
                break;
            default:
                break;
        }
    }
}
