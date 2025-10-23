package com.smarthome.smart_home.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.smarthome.smart_home.model.Sensor;
import com.smarthome.smart_home.repository.AutomationRepository;
import com.smarthome.smart_home.repository.SensorRepository;

import lombok.RequiredArgsConstructor;

import com.smarthome.smart_home.model.AutomationRule;
import lombok.extern.slf4j.Slf4j;
@Service
@Slf4j
@RequiredArgsConstructor
public class AutomationService {
    // private final SensorService sensorService;
    private final DeviceService deviceService;
    private final AutomationRepository automationRepository;
    private final SensorRepository sensorRepository;

    public List<AutomationRule> getAllRules() {
        return automationRepository.findAll();
    }
    // Затриггерить все правила
    public List<AutomationRule> triggerAll() {
        List<AutomationRule> rules = automationRepository.findByEnabled(true);
        List<AutomationRule> actionedRules = new ArrayList<>();
        log.debug(rules.toString());
        for (AutomationRule rule : rules) {
            if (checkCondition(rule, sensorRepository.findById(rule.getTriggerSensorId()).orElseThrow())){
                goAction(rule);
                actionedRules.add(rule);
            }
        }
        return actionedRules;
    }

    
    // Триггер сенсора
    public void sensorTrigger(Sensor sensor) {
        List<AutomationRule> rules = automationRepository.findByEnabledAndTriggerSensorId(true, sensor.getId());
        // List<AutomationRule> actionedRules = new ArrayList<>();
        for (AutomationRule rule : rules) {
            if (checkCondition(rule, sensor)) {
                goAction(rule);
                // actionedRules.add(rule);
            }
        }
        // return actionedRules;
        
    }

    private boolean checkCondition(AutomationRule rule, Sensor sensor) {
        if (rule.getTriggerEvent().equals("greater")) {
            return sensor.getValue() > rule.getTriggerValue();
        } else if (rule.getTriggerEvent().equals("less")) {
            return sensor.getValue() < rule.getTriggerValue();
        } else {
            return false;
        }
    }

    private void goAction(AutomationRule rule) {
        if (rule.getAction().equals("turnOn")) {
            deviceService.turnOn(rule.getTriggerDeviceId());
        } else if (rule.getAction().equals("turnOff")) {
            deviceService.turnOff(rule.getTriggerDeviceId());
        }
    }
}
