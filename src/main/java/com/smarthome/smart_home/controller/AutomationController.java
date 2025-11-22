package com.smarthome.smart_home.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.smarthome.smart_home.dto.automation.AutomationRuleDTO;
import com.smarthome.smart_home.dto.automation.AutomationRuleResponseDTO;
import com.smarthome.smart_home.enums.automation.Action;
import com.smarthome.smart_home.enums.automation.TriggerEvent;
import com.smarthome.smart_home.mappers.AutomationRuleMapper;
import com.smarthome.smart_home.model.AutomationRule;
import com.smarthome.smart_home.service.automation.AutomationService;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
@Slf4j
public class AutomationController {
    private final AutomationService automationService;
    private final AutomationRuleMapper automationRuleMapper;

    public AutomationController(AutomationService automationService, AutomationRuleMapper automationRuleMapper) {
        this.automationService = automationService;
        this.automationRuleMapper = automationRuleMapper;
    }

    // Получить все правила
    @GetMapping()
    public ResponseEntity<Page<AutomationRuleResponseDTO>> getAllRules(
            @RequestParam(required = false) Boolean enabled,
            @RequestParam(required = false) TriggerEvent triggerEvent,
            @RequestParam(required = false) Integer triggerValue,
            @RequestParam(required = false) Long triggerDeviceId,
            @RequestParam(required = false) Long triggerSensorId,
            @RequestParam(required = false) Action action,
            @RequestParam(required = false) Double actionValue,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection) {

        log.info("Getting all automation rules with filters - enabled: {}, triggerEvent: {}, triggerValue: {}, "
                + "triggerDeviceId: {}, triggerSensorId: {}, action: {}, actionValue: {}, page: {}, size: {}, sortBy: {}, sortDirection: {}",
                enabled, triggerEvent, triggerValue, triggerDeviceId, triggerSensorId, action, actionValue, page, size,
                sortBy, sortDirection);
        Sort sort = sortDirection.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<AutomationRuleResponseDTO> rulesPage = automationService.getRulesByFilters(
                enabled, triggerEvent, triggerValue, triggerDeviceId,
                triggerSensorId, action, actionValue, pageable)
                .map(automationRuleMapper::toResponseDTO);
        log.debug("Retrieved {} automation rules out of {} total", rulesPage.getNumberOfElements(),
                rulesPage.getTotalElements());
        return ResponseEntity.ok(rulesPage);
    }

    @GetMapping("/triggerAll")
    public ResponseEntity<List<AutomationRuleResponseDTO>> triggerAllRules() {
        log.info("Triggering all automation rules");
        List<AutomationRule> triggeredRules = automationService.triggerAll();
        log.info("Successfully triggered {} automation rules", triggeredRules.size());
        log.debug("Triggered rule IDs: {}",
                triggeredRules.stream()
                        .map(AutomationRule::getId)
                        .collect(Collectors.toList()));
        return ResponseEntity.ok(triggeredRules.stream().map(automationRuleMapper::toResponseDTO)
                .collect(Collectors.toList()));
    }

    @PostMapping
    public ResponseEntity<AutomationRuleResponseDTO> createAutomationRule(
            @Valid @RequestBody AutomationRuleDTO automationRuleDTO) {
        log.info("Creating new automation rule");
        log.debug("Automation rule DTO: {}", automationRuleDTO);

        AutomationRuleResponseDTO createdRule = automationService.createRule(automationRuleDTO);

        log.info("Successfully created automation rule with ID: {}", createdRule.getId());
        log.debug("Created automation rule details: {}", createdRule);

        return ResponseEntity.ok(createdRule);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AutomationRuleResponseDTO> updateRule(
            @Valid @RequestBody AutomationRuleDTO automationRuleDTO,
            @PathVariable Long id) {
        log.info("Updating automation rule with ID: {}", id);
        log.debug("Update DTO for rule {}: {}", id, automationRuleDTO);

        AutomationRuleResponseDTO updatedRule = automationService.updateRule(id, automationRuleDTO);

        log.info("Successfully updated automation rule with ID: {}", id);
        log.debug("Updated automation rule details: {}", updatedRule);

        return ResponseEntity.ok(updatedRule);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<AutomationRuleResponseDTO> partiallyUpdateRule(
            @RequestBody AutomationRuleDTO automationRuleDTO,
            @PathVariable Long id) {
        log.info("Partially updating automation rule with ID: {}", id);
        log.debug("Partial update DTO for rule {}: {}", id, automationRuleDTO);

        AutomationRuleResponseDTO updatedRule = automationService.partiallyUpdateRule(id, automationRuleDTO);

        log.info("Successfully partially updated automation rule with ID: {}", id);
        log.debug("Partially updated automation rule details: {}", updatedRule);

        return ResponseEntity.ok(updatedRule);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAutomationRule(@PathVariable Long id) {
        log.info("Deleting automation rule with ID: {}", id);

        automationService.deleteRule(id);

        log.info("Successfully deleted automation rule with ID: {}", id);
        return ResponseEntity.noContent().build();
    }

}
