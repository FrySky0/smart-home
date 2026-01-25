package com.smarthome.smart_home.service.automation;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.smarthome.smart_home.dto.AutomationRuleValidatable;
import com.smarthome.smart_home.dto.create.AutomationRuleCreateDTO;
import com.smarthome.smart_home.dto.response.AutomationRuleResponseDTO;
import com.smarthome.smart_home.dto.update.patch.AutomationRulePatchDTO;
import com.smarthome.smart_home.dto.update.put.AutomationRulePutDTO;
import com.smarthome.smart_home.enums.automation.Action;
import com.smarthome.smart_home.enums.automation.TriggerEvent;
import com.smarthome.smart_home.events.SensorUpdatedEvent;
import com.smarthome.smart_home.mappers.AutomationRuleMapper;
import com.smarthome.smart_home.model.AutomationRule;
import com.smarthome.smart_home.model.Device;
import com.smarthome.smart_home.model.Sensor;
import com.smarthome.smart_home.repository.AutomationRepository;
import com.smarthome.smart_home.repository.SensorRepository;
import com.smarthome.smart_home.service.DeviceService;
import com.smarthome.smart_home.service.SensorService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
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

    public Page<AutomationRule> getRulesByFilters(
            String name,
            String description,
            Boolean enabled,
            TriggerEvent triggerEvent,
            LocalTime triggerTime,
            Double triggerValue,
            UUID triggerDeviceUuid,
            UUID triggerSensorUuid,
            Action action,
            Double actionValue,
            Pageable pageable) {
        log.debug(
                "Fetching paginated automation rules with filters - name: {}, descriptiong: {}, enabled: {}, triggerEvent: {}, triggerValue: {}, triggerDeviceUuid: {}, triggerSensorUuid: {}, action: {}, actionValue: {}, pageable: {}",
                name,description,enabled, triggerEvent, triggerValue, triggerDeviceUuid, triggerSensorUuid, action, actionValue, pageable);
        String searchName = null;
        if (name != null && !name.isBlank()) {
            searchName = "%" + name.toLowerCase() + "%";
        }
        String searchDescription = null;
        if (description != null && !description.isBlank()) {
            searchDescription = "%" + description.toLowerCase() + "%";
        }
        log.warn("searchName: {}, searchDescription: {}", searchName, searchDescription);
        Page<AutomationRule> rules = automationRepository.findByFilters(searchName,searchDescription,enabled, triggerEvent, triggerValue,
                triggerTime, triggerDeviceUuid, triggerSensorUuid,
                action, actionValue, pageable);

        log.info("Retrieved {} automation rules (page {}/{} with {} total elements) matching the specified filters",
                rules.getNumberOfElements(), rules.getNumber(), rules.getTotalPages(), rules.getTotalElements());
        return rules;
    }

    public AutomationRule getRuleById(Long id) {
        log.info("Fetching automation rule by ID: {}", id);
        AutomationRule rule = automationRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Automation rule not found with ID: {}", id);
                    return new RuntimeException("Automation rule not found with id: " + id);
                });
        log.debug("Successfully retrieved automation rule: {} (ID: {})", rule.getName(), rule.getId());
        return rule;
    }

    // Затриггерить все правила
    public List<AutomationRule> triggerAll() {
        log.info("Triggering all enabled automation rules");
        List<AutomationRule> rules = automationRepository.findByEnabled(true);
        List<AutomationRule> actionedRules = new ArrayList<>();

        log.debug("Found {} enabled rules to process", rules.size());

        for (AutomationRule rule : rules) {
            if (checkCondition(rule, rule.getSensor())) {
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

    public AutomationRuleResponseDTO createRule(AutomationRuleCreateDTO dto) {
        log.info("Creating new automation rule with name: {}", dto.getName());

        Device device = deviceService.getDeviceByUuid(dto.getDeviceUuid());

        validateRuleLogic(dto,device);
        
        Sensor sensor = null;
        if (dto.getSensorUuid() != null){
            sensor = sensorService.getSensorByUuid(dto.getSensorUuid());
        }

        AutomationRule rule = automationRuleMapper.toEntity(dto, device, sensor);
        automationRepository.save(rule);

        log.info("Successfully created automation rule with ID: {} and name: {}", rule.getId(), rule.getName());
        return automationRuleMapper.toResponseDTO(rule);
    }

    private void validateRuleLogic(AutomationRuleValidatable dto, Device device){
        if (dto.getTriggerEvent() == null){
            log.error("Cannot create rule - Trigger event must be specified");
            throw new RuntimeException("Trigger event must be specified");
        }
        if (dto.getTriggerEvent() == TriggerEvent.TIME){
            if (dto.getTriggerTime() == null){
                log.error("Cannot create rule - TIME trigger event requires a trigger time");
                throw new RuntimeException("TIME trigger event requires a trigger time");
            }
        }
        else {
            if (dto.getSensorUuid() == null){
                log.error("Cannot create rule - Non-TIME trigger event requires a sensor UUID");
                throw new RuntimeException("Non-TIME trigger event requires a sensor UUID");
            }
            if (dto.getTriggerValue() == null){
                log.error("Cannot create rule - Non-TIME trigger event requires a trigger value");
                throw new RuntimeException("Non-TIME trigger event requires a trigger value");
            }
        }
        if (dto.getAction() == Action.SET_VALUE){
            if (dto.getActionValue() == null){
                log.error("Cannot create rule - SET_VALUE action requires an action value");
                throw new RuntimeException("SET_VALUE action requires an action value");
            }
            if (!device.getType().hasValue()){
                log.error("Cannot create rule - Device type {} does not support action values", device.getType());
                throw new RuntimeException("Device type " + device.getType() + " does not support action values");
            }
        }
    }

    public AutomationRuleResponseDTO updateRule(Long id, AutomationRulePutDTO dto) {
        log.info("Updating automation rule with ID: {}", id);

        AutomationRule existingRule = automationRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Automation rule not found with ID: {}", id);
                    return new RuntimeException("Automation rule not found with id: " + id);
                });
        
        Device device = deviceService.getDeviceByUuid(dto.getDeviceUuid());

        validateRuleLogic(dto, device);

        Sensor sensor = null;
        if (dto.getSensorUuid() != null){
            sensor = sensorService.getSensorByUuid(dto.getSensorUuid());
        }

        log.debug("Updating rule properties for rule ID: {}", id);
        existingRule.setName(dto.getName());
        existingRule.setDescription(dto.getDescription());
        existingRule.setEnabled(dto.getEnabled());
        existingRule.setDevice(device);
        existingRule.setSensor(sensor);
        existingRule.setTriggerEvent(dto.getTriggerEvent());
        existingRule.setTriggerValue(dto.getTriggerValue());
        existingRule.setTriggerTime(dto.getTriggerTime());
        existingRule.setAction(dto.getAction());
        existingRule.setActionValue(dto.getActionValue());

        automationRepository.save(existingRule);
        log.info("Successfully updated automation rule with ID: {}", id);

        return automationRuleMapper.toResponseDTO(existingRule);
    }

    public AutomationRuleResponseDTO partiallyUpdateRule(Long id, AutomationRulePatchDTO dto) {
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
        if (dto.getDeviceUuid() != null) {
            Device device = deviceService.getDeviceByUuid(dto.getDeviceUuid());
            log.debug("Updating trigger device to ID: {}", dto.getDeviceUuid());
            existingRule.setDevice(device);
        }
        if (dto.getSensorUuid() != null) {
            Sensor sensor = sensorService.getSensorByUuid(dto.getSensorUuid());
            log.debug("Updating trigger sensor to ID: {}", dto.getSensorUuid());
            existingRule.setSensor(sensor);
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

        List<AutomationRule> rules = automationRepository.findByEnabledAndSensorId(true, sensor.getId());
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
                rule.getName(), rule.getAction(), rule.getDevice().getId());

        switch (rule.getAction()) {
            case TURN_ON:
                log.info("Turning on device ID: {} for rule '{}'", rule.getDevice().getId(), rule.getName());
                deviceService.turnOn(rule.getDevice());
                break;
            case TURN_OFF:
                log.info("Turning off device ID: {} for rule '{}'", rule.getDevice().getId(), rule.getName());
                deviceService.turnOff(rule.getDevice());
                break;
            case SET_VALUE:
                log.info("Setting value to {} for device ID: {} for rule '{}'",
                        rule.getActionValue(), rule.getDevice().getId(), rule.getName());
                deviceService.setValue(rule.getDevice(), rule.getActionValue());
                break;
            default:
                log.warn("Unknown action type: {} for rule ID: {}", rule.getAction(), rule.getId());
                break;
        }

        log.debug("Action execution completed for rule '{}'", rule.getName());
    }

    @Scheduled(cron = "0 * * * * *") // Каждый час в начале часа
    @Transactional
    public void checkTimeBasedRules(){
        LocalTime now = LocalTime.now().withSecond(0).withNano(0);
        log.info("Checking time-based automation rules at {}", now);
        List<AutomationRule> rulesToExecute = automationRepository.findActiveTimeRules(now);
        if (!rulesToExecute.isEmpty()){
            log.info("Found {} time-based rules to execute", rulesToExecute.size());
            for (AutomationRule rule : rulesToExecute){
                log.info("Executing time-based rule: {} (ID: {})", rule.getName(), rule.getId());
                goAction(rule);
            }
        }
    }
}
