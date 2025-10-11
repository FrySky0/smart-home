package com.smarthome.smart_home.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.smarthome.smart_home.model.Sensor;
import com.smarthome.smart_home.enums.SensorType;
import com.smarthome.smart_home.service.SensorService;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/api/sensors")
public class SensorController {
    private final SensorService sensorService;
    public SensorController(SensorService sensorService){
        this.sensorService = sensorService;
    }
    @GetMapping
    public ResponseEntity<List<Sensor>> getAllSensors(
        @RequestParam(required = false) Long roomId,
        @RequestParam(required = false) SensorType type
    ) {
        if (roomId != null || type != null){
            return ResponseEntity.ok(sensorService.getSensorsByFilters(roomId, type));
        }
        return ResponseEntity.ok(sensorService.getAllSensors());
    }
    @GetMapping("/{id}")
    public ResponseEntity<Sensor> getSensorById(@PathVariable Long id) {
        return ResponseEntity.ok(sensorService.getSensorById(id));
    }
    
}
