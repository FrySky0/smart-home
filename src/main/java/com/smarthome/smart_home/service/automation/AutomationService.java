package com.smarthome.smart_home.service.automation;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
        log.debug("Fetching all automation rules");
        List<AutomationRule> rules = automationRepository.findAll();
        log.info("Retrieved {} automation rules", rules.size());
        return rules;
    }

    public List<AutomationRule> getRulesByFilters(
            Boolean enabled,
            TriggerEvent triggerEvent,
            Integer triggerValue,
            Long triggerDeviceId,
            Long triggerSensorId,
            Action action,
            Double actionValue) {
        log.debug(
                "Fetching automation rules with filters - enabled: {}, triggerEvent: {}, triggerValue: {}, triggerDeviceId: {}, triggerSensorId: {}, action: {}, actionValue: {}",
                enabled, triggerEvent, triggerValue, triggerDeviceId, triggerSensorId, action, actionValue);

        List<AutomationRule> rules = automationRepository.findByFilters(enabled, triggerEvent, triggerValue,
                triggerDeviceId, triggerSensorId,
                action, actionValue);

        log.info("Retrieved {} automation rules matching the specified filters", rules.size());
        return rules;
    }

    public Page<AutomationRule> getRulesByFilters(
            Boolean enabled,
            TriggerEvent triggerEvent,
            Integer triggerValue,
            Long triggerDeviceId,
            Long triggerSensorId,
            Action action,
            Double actionValue,
            Pageable pageable) {
        log.debug(
                "Fetching paginated automation rules with filters - enabled: {}, triggerEvent: {}, triggerValue: {}, triggerDeviceId: {}, triggerSensorId: {}, action: {}, actionValue: {}, pageable: {}",
                enabled, triggerEvent, triggerValue, triggerDeviceId, triggerSensorId, action, actionValue, pageable);

        Page<AutomationRule> rules = automationRepository.findByFilters(enabled, triggerEvent, triggerValue,
                triggerDeviceId, triggerSensorId,
                action, actionValue, pageable);

        log.info("Retrieved {} automation rules (page {}/{} with {} total elements) matching the specified filters",
                rules.getNumberOfElements(), rules.getNumber(), rules.getTotalPages(), rules.getTotalElements());
        return rules;
    }

    // Затриггерить все правила
    public List<AutomationRule> triggerAll() {
        log.info("Triggering all enabled automation rules");
        List<AutomationRule> rules = automationRepository.findByEnabled(true);
        List<AutomationRule> actionedRules = new ArrayList<>();

        log.debug("Found {} enabled rules to process", rules.size());

        for (AutomationRule rule : rules) {
            if (checkCondition(rule, rule.getTriggerSensor())) {
                log.debug("Rule '{}' condition met, executing action", rule.getName());
                goAction(rule);
                actionedRules.add(rule);
            } else {
                log.debug("Rule '{}' condition not met", rule.getName());
            }
        }

        log.info("Executed actions for {} automation rules", actionedRules.size());
        return actionedRules;
    }

    public AutomationRuleResponseDTO createRule(AutomationRuleDTO dto) {
        log.info("Creating new automation rule with name: {}", dto.getName());

        Device device = deviceService.getDeviceById(dto.getTriggerDeviceId());
        if (!device.getType().hasValue() && dto.getAction() == Action.SET_VALUE) {
            log.error("Cannot create rule - device with type {} does not support SET_VALUE action", device.getType());
            throw new RuntimeException("Device with type " + device.getType() + " does not have a value");
        }

        log.debug("Device type validation passed for device: {}", device.getType());
        Sensor sensor = sensorService.getSensorById(dto.getTriggerSensorId());
        AutomationRule rule = automationRuleMapper.toEntity(dto, device, sensor);
        automationRepository.save(rule);

        log.info("Successfully created automation rule with ID: {} and name: {}", rule.getId(), rule.getName());
        return automationRuleMapper.toResponseDTO(rule);
    }

    public AutomationRuleResponseDTO updateRule(Long id, AutomationRuleDTO dto) {
        log.info("Updating automation rule with ID: {}", id);

        AutomationRule existingRule = automationRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Automation rule not found with ID: {}", id);
                    return new RuntimeException("Automation rule not found with id: " + id);
                });

        Device device = deviceService.getDeviceById(dto.getTriggerDeviceId());
        Sensor sensor = sensorService.getSensorById(dto.getTriggerSensorId());

        log.debug("Updating rule properties for rule ID: {}", id);
        existingRule.setName(dto.getName());
        existingRule.setDescription(dto.getDescription());
        existingRule.setTriggerDevice(device);
        existingRule.setTriggerSensor(sensor);
        existingRule.setTriggerEvent(dto.getTriggerEvent());
        existingRule.setTriggerValue(dto.getTriggerValue());
        existingRule.setAction(dto.getAction());
        existingRule.setActionValue(dto.getActionValue());

        automationRepository.save(existingRule);
        log.info("Successfully updated automation rule with ID: {}", id);

        return automationRuleMapper.toResponseDTO(existingRule);
    }

    public AutomationRuleResponseDTO partiallyUpdateRule(Long id, AutomationRuleDTO dto) {
        log.info("Partially updating automation rule with ID: {}", id);

        AutomationRule existingRule = automationRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Automation rule not found with ID: {}", id);
                    return new RuntimeException("Automation rule not found with id: " + id);
                });

        if (dto.getName() != null) {
            log.debug("Updating rule name to: {}", dto.getName());
            existingRule.setName(dto.getName());
        }
        if (dto.getDescription() != null) {
            log.debug("Updating rule description");
            existingRule.setDescription(dto.getDescription());
        }
        if (dto.getTriggerDeviceId() != null) {
            Device device = deviceService.getDeviceById(dto.getTriggerDeviceId());
            log.debug("Updating trigger device to ID: {}", dto.getTriggerDeviceId());
            existingRule.setTriggerDevice(device);
        }
        if (dto.getTriggerSensorId() != null) {
            Sensor sensor = sensorService.getSensorById(dto.getTriggerSensorId());
            log.debug("Updating trigger sensor to ID: {}", dto.getTriggerSensorId());
            existingRule.setTriggerSensor(sensor);
        }
        if (dto.getTriggerEvent() != null) {
            log.debug("Updating trigger event to: {}", dto.getTriggerEvent());
            existingRule.setTriggerEvent(dto.getTriggerEvent());
        }
        if (dto.getTriggerValue() != null) {
            log.debug("Updating trigger value to: {}", dto.getTriggerValue());
            existingRule.setTriggerValue(dto.getTriggerValue());
        }
        if (dto.getAction() != null) {
            log.debug("Updating action to: {}", dto.getAction());
            existingRule.setAction(dto.getAction());
        }
        if (dto.getActionValue() != null) {
            log.debug("Updating action value to: {}", dto.getActionValue());
            existingRule.setActionValue(dto.getActionValue());
        }

        automationRepository.save(existingRule);
        log.info("Successfully partially updated automation rule with ID: {}", id);

        return automationRuleMapper.toResponseDTO(existingRule);
    }

    public void deleteRule(Long id) {
        log.info("Deleting automation rule with ID: {}", id);
        automationRepository.deleteById(id);
        log.info("Successfully deleted automation rule with ID: {}", id);
    }

    @EventListener
    public void sensorTrigger(SensorUpdatedEvent event) {
        Sensor sensor = event.getSensor();
        log.debug("Processing sensor update event for sensor ID: {} with value: {}", sensor.getId(), sensor.getValue());

        List<AutomationRule> rules = automationRepository.findByEnabledAndTriggerSensorId(true, sensor.getId());
        log.debug("Found {} enabled rules associated with sensor ID: {}", rules.size(), sensor.getId());

        for (AutomationRule rule : rules) {
            log.debug("Checking condition for rule '{}' (ID: {})", rule.getName(), rule.getId());
            if (checkCondition(rule, sensor)) {
                log.info("Rule '{}' condition met for sensor ID: {} with value: {}, executing action",
                        rule.getName(), sensor.getId(), sensor.getValue());
                goAction(rule);
            } else {
                log.debug("Rule '{}' condition not met for sensor value: {}", rule.getName(), sensor.getValue());
            }
        }
    }

    private boolean checkCondition(AutomationRule rule, Sensor sensor) {
        log.trace("Checking condition for rule '{}': {} {} {} (sensor value: {})",
                rule.getName(), sensor.getValue(), rule.getTriggerEvent(), rule.getTriggerValue(), sensor.getValue());

        boolean conditionMet;
        switch (rule.getTriggerEvent()) {
            case GREATER_THAN:
                conditionMet = sensor.getValue() > rule.getTriggerValue();
                break;
            case LESS_THAN:
                conditionMet = sensor.getValue() < rule.getTriggerValue();
                break;
            case EQUALS:
                conditionMet = sensor.getValue().equals(rule.getTriggerValue());
                break;
            default:
                log.warn("Unknown trigger event type: {} for rule ID: {}", rule.getTriggerEvent(), rule.getId());
                conditionMet = false;
        }

        log.trace("Condition check result for rule '{}': {}", rule.getName(), conditionMet);
        return conditionMet;
    }

    private void goAction(AutomationRule rule) {
        log.debug("Executing action for rule '{}': {} on device ID: {}",
                rule.getName(), rule.getAction(), rule.getTriggerDevice().getId());

        switch (rule.getAction()) {
            case TURN_ON:
                log.info("Turning on device ID: {} for rule '{}'", rule.getTriggerDevice().getId(), rule.getName());
                deviceService.turnOn(rule.getTriggerDevice());
                break;
            case TURN_OFF:
                log.info("Turning off device ID: {} for rule '{}'", rule.getTriggerDevice().getId(), rule.getName());
                deviceService.turnOff(rule.getTriggerDevice());
                break;
            case SET_VALUE:
                log.info("Setting value to {} for device ID: {} for rule '{}'",
                        rule.getActionValue(), rule.getTriggerDevice().getId(), rule.getName());
                deviceService.setValue(rule.getTriggerDevice(), rule.getActionValue());
                break;
            default:
                log.warn("Unknown action type: {} for rule ID: {}", rule.getAction(), rule.getId());
                break;
        }

        log.debug("Action execution completed for rule '{}'", rule.getName());
    }
}
