package com.smarthome.smart_home.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.smarthome.smart_home.dto.automation.AutomationRuleDTO;
import com.smarthome.smart_home.dto.automation.AutomationRuleResponseDTO;
import com.smarthome.smart_home.mappers.AutomationRuleMapper;
import com.smarthome.smart_home.model.AutomationRule;
import com.smarthome.smart_home.service.automation.AutomationService;

import jakarta.validation.Valid;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/automation")
public class AutomationController {
    private final AutomationService automationService;
    private final AutomationRuleMapper automationRuleMapper;

    public AutomationController(AutomationService automationService, AutomationRuleMapper automationRuleMapper) {
        this.automationService = automationService;
        this.automationRuleMapper = automationRuleMapper;
    }

    // Получить все правила
    @GetMapping()
    public ResponseEntity<List<AutomationRuleResponseDTO>> getAllRules(
            @RequestParam(required = false) Boolean enabled,
            @RequestParam(required = false) String triggerEvent,
            @RequestParam(required = false) Integer triggerValue,
            @RequestParam(required = false) Long triggerDeviceId,
            @RequestParam(required = false) Long triggerSensorId,
            @RequestParam(required = false) String action) {
        List<AutomationRule> rules;
        if (enabled != null || triggerEvent != null || triggerValue != null || triggerDeviceId != null
                || triggerSensorId != null || action != null) {
            rules = automationService.getRulesByFilters(enabled, triggerEvent, triggerValue, triggerDeviceId,
                    triggerSensorId, action);
        } else {
            rules = automationService.getAllRules();
        }
        return ResponseEntity.ok(rules.stream()
                .map(automationRuleMapper::toResponseDTO)
                .collect(Collectors.toList()));
    }

    @GetMapping("/triggerAll")
    public ResponseEntity<List<AutomationRuleResponseDTO>> triggerAllRules() {
        List<AutomationRule> triggeredRules = automationService.triggerAll();
        return ResponseEntity.ok(triggeredRules.stream().map(automationRuleMapper::toResponseDTO)
                .collect(Collectors.toList()));
    }

    @PostMapping
    public ResponseEntity<AutomationRuleResponseDTO> createAutomationRule(
            @Valid @RequestBody AutomationRuleDTO automationRuleDTO) {
        return ResponseEntity.ok(automationService.createRule(automationRuleDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AutomationRuleResponseDTO> updateRule(
            @Valid @RequestBody AutomationRuleDTO automationRuleDTO,
            @PathVariable Long id) {
        return ResponseEntity.ok(automationService.updateRule(id, automationRuleDTO));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<AutomationRuleResponseDTO> partiallyUpdateRule(
            @RequestBody AutomationRuleDTO automationRuleDTO,
            @PathVariable Long id) {
        return ResponseEntity.ok(automationService.partiallyUpdateRule(id, automationRuleDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAutomationRule(@PathVariable Long id) {
        automationService.deleteRule(id);
        return ResponseEntity.noContent().build();
    }

}
