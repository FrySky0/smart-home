package com.smarthome.smart_home.controller;

import java.time.LocalTime;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.smarthome.smart_home.dto.create.AutomationRuleCreateDTO;
import com.smarthome.smart_home.dto.response.AutomationRuleResponseDTO;
import com.smarthome.smart_home.dto.update.patch.AutomationRulePatchDTO;
import com.smarthome.smart_home.dto.update.put.AutomationRulePutDTO;
import com.smarthome.smart_home.enums.activitylog.ComponentName;
import com.smarthome.smart_home.enums.activitylog.LogAction;
import com.smarthome.smart_home.enums.automation.Action;
import com.smarthome.smart_home.enums.automation.TriggerEvent;
import com.smarthome.smart_home.mappers.AutomationRuleMapper;
import com.smarthome.smart_home.model.AutomationRule;
import com.smarthome.smart_home.service.AutomationService;
import com.smarthome.smart_home.service.LogService;
import com.smarthome.smart_home.service.TelegramService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/automations")
@Tag(name = "Automation Rules", description = "Управление правилами автоматизации")
@Slf4j
public class AutomationController {
    private final AutomationService automationService;
    private final AutomationRuleMapper automationRuleMapper;
    private final TelegramService telegramService;
    private final LogService logService;

    public AutomationController(AutomationService automationService, AutomationRuleMapper automationRuleMapper, TelegramService telegramService,LogService logService) {
        this.automationService = automationService;
        this.automationRuleMapper = automationRuleMapper;
        this.telegramService = telegramService;
        this.logService = logService;
    }

    // Получить все правила
    @Operation(summary = "Получить правила автоматизации с возможностью фильтрации", description="Возвращает страницу правил автоматизации на основе предоставленных фильтров.")
    @GetMapping()
    public ResponseEntity<Page<AutomationRuleResponseDTO>> getAllRules(
            @Parameter(description="Поиск по названию (частичное совпадение)") @RequestParam(required = false) String name,
            @Parameter(description="Поиск по описанию (частичное совпадение)") @RequestParam(required = false) String description,
            @Parameter(description="Поиск состоянию (ON, OFF)") @RequestParam(required = false) Boolean enabled,
            @Parameter(description="Поиск событию триггера") @RequestParam(required = false) TriggerEvent triggerEvent,
            @Parameter(description="Поиск по времени триггера (Есть только у TriggerEvent TIME)") @RequestParam(required = false) LocalTime triggerTime,
            @Parameter(description="Поиск по значению у сенсора") @RequestParam(required = false) Double triggerValue,
            @Parameter(description="Поиск по UUID девайса") @RequestParam(required = false) UUID triggerDeviceUuid,
            @Parameter(description="Поиск по UUID сенсора") @RequestParam(required = false) UUID triggerSensorUuid,
            @Parameter(description="Поиск по действию") @RequestParam(required = false) Action action,
            @Parameter(description="Поиск задаваемому значению девайсу (только если Action = SET_VALUE)") @RequestParam(required = false) Double actionValue,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description="Сортировка по asc-возрастанию, desc-убыванию") @RequestParam(defaultValue = "asc") String sortDirection) {

        log.info("Getting all automation rules with filters - enabled: {}, triggerEvent: {}, triggerValue: {}, "
                + "triggerDeviceUuid: {}, triggerSensorUuid: {}, action: {}, actionValue: {}, page: {}, size: {}, sortBy: {}, sortDirection: {}",
                enabled, triggerEvent, triggerValue, triggerDeviceUuid, triggerSensorUuid, action, actionValue, page, size,
                sortBy, sortDirection);
        Sort sort = sortDirection.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<AutomationRuleResponseDTO> rulesPage = automationService.getRulesByFilters(
                name,description, enabled, triggerEvent,triggerTime, triggerValue, triggerDeviceUuid,
                triggerSensorUuid, action, actionValue, pageable)
                .map(automationRuleMapper::toResponseDTO);
        log.debug("Retrieved {} automation rules out of {} total", rulesPage.getNumberOfElements(),
                rulesPage.getTotalElements());
        return ResponseEntity.ok(rulesPage);
    }
    @Operation(summary = "Получить правило автоматизации по ID", description="Возвращает правило автоматизации по его уникальному идентификатору.")
    @GetMapping("/{id}")
    public ResponseEntity<AutomationRuleResponseDTO> getRuleById(@PathVariable Long id) {
        log.info("Getting automation rule by ID: {}", id);
        AutomationRule rule = automationService.getRuleById(id);
        AutomationRuleResponseDTO ruleDTO = automationRuleMapper.toResponseDTO(rule);
        log.info("Successfully retrieved automation rule with ID: {}", id);
        return ResponseEntity.ok(ruleDTO);
    }
    
    // @GetMapping("/triggerAll")
    // @PreAuthorize("hasAnyRole('USER','ADMIN')")
    // public ResponseEntity<List<AutomationRuleResponseDTO>> triggerAllRules() {
    //     log.info("Triggering all automation rules");
    //     List<AutomationRule> triggeredRules = automationService.triggerAll();
    //     log.info("Successfully triggered {} automation rules", triggeredRules.size());
    //     log.debug("Triggered rule IDs: {}",
    //             triggeredRules.stream()
    //                     .map(AutomationRule::getId)
    //                     .collect(Collectors.toList()));
    //     return ResponseEntity.ok(triggeredRules.stream().map(automationRuleMapper::toResponseDTO)
    //             .collect(Collectors.toList()));
    // }
    @Operation(summary = "Создать новое правило автоматизации", description="Создает новое правило автоматизации на основе предоставленных данных.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Правило успешно создано"),
        @ApiResponse(responseCode = "400", description = "Ошибка валидации логики правила")
    })
    @PostMapping
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<AutomationRuleResponseDTO> createAutomationRule(
            @Valid @RequestBody AutomationRuleCreateDTO automationRuleCreateDTO) {
        log.info("Creating new automation rule");
        log.debug("Automation rule DTO: {}", automationRuleCreateDTO);

        AutomationRuleResponseDTO createdRule = automationService.createRule(automationRuleCreateDTO);

        log.info("Successfully created automation rule with ID: {}", createdRule.getId());
        log.debug("Created automation rule details: {}", createdRule);
        telegramService.sendLog("New automation rule created: " + createdRule.getName() + " (ID: " + createdRule.getId() + ")");
        logService.logEntity(ComponentName.AutomationRule, LogAction.CREATED, createdRule);
        return ResponseEntity.ok(createdRule);
    }

    @Operation(summary = "Полностью обновить правило автоматизации", description="Полностью обновляет существующее правило автоматизации по его уникальному идентификатору на основе предоставленных данных.")
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<AutomationRuleResponseDTO> updateRule(
            @Valid @RequestBody AutomationRulePutDTO automationRulePutDTO,
            @PathVariable Long id) {
        log.info("Updating automation rule with ID: {}", id);
        log.debug("Update DTO for rule {}: {}", id, automationRulePutDTO);

        AutomationRuleResponseDTO updatedRule = automationService.updateRule(id, automationRulePutDTO);

        log.info("Successfully updated automation rule with ID: {}", id);
        log.debug("Updated automation rule details: {}", updatedRule);
        telegramService.sendLog("Automation rule " + updatedRule.getName() + " (ID: " + updatedRule.getId() + ") fully updated");
        logService.logEntity(ComponentName.AutomationRule, LogAction.UPDATED, updatedRule);
        return ResponseEntity.ok(updatedRule);
    }
    @Operation(summary = "Частично обновить правило автоматизации", description="Частично обновляет существующее правило автоматизации по его уникальному идентификатору на основе предоставленных данных.")
    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<AutomationRuleResponseDTO> partiallyUpdateRule(
            @RequestBody AutomationRulePatchDTO automationRulePatchDTO,
            @PathVariable Long id) {
        log.info("Partially updating automation rule with ID: {}", id);
        log.debug("Partial update DTO for rule {}: {}", id, automationRulePatchDTO);

        AutomationRuleResponseDTO updatedRule = automationService.partiallyUpdateRule(id, automationRulePatchDTO);

        log.info("Successfully partially updated automation rule with ID: {}", id);
        log.debug("Partially updated automation rule details: {}", updatedRule);
        telegramService.sendLog("Automation rule " + updatedRule.getName() + " (ID: " + updatedRule.getId() + ") partially updated");
        logService.logEntity(ComponentName.AutomationRule, LogAction.UPDATED, updatedRule);
        return ResponseEntity.ok(updatedRule);
    }

    @Operation(summary = "Удалить правило автоматизации", description="Удаляет существующее правило автоматизации по его уникальному идентификатору.")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<Void> deleteAutomationRule(@PathVariable Long id) {
        log.info("Deleting automation rule with ID: {}", id);

        automationService.deleteRule(id);

        log.info("Successfully deleted automation rule with ID: {}", id);
        telegramService.sendLog("Automation rule with ID " + id + " has been deleted.");
        logService.log(ComponentName.AutomationRule, LogAction.DELETED, "ID:"+id);
        return ResponseEntity.noContent().build();
    }

}
