package com.smarthome.smart_home.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.smarthome.smart_home.model.AutomationRule;
import com.smarthome.smart_home.service.AutomationService;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/api/automation")
public class AutomationController {
    private final AutomationService automationService;

    public AutomationController(AutomationService automationService) {
        this.automationService = automationService;
    }

    // Получить все правила
    @GetMapping()
    public ResponseEntity<List<AutomationRule>> getAllRules() {
        return ResponseEntity.ok(automationService.getAllRules());
    }

    @GetMapping("/triggerAll")
    public ResponseEntity<List<AutomationRule>> triggerAllRules() {
        return ResponseEntity.ok(automationService.triggerAll());
    }
    

    
}
