package com.smarthome.smart_home.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.smarthome.smart_home.model.Sensor;
import com.smarthome.smart_home.service.DeviceService;
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
    public ResponseEntity<List<Sensor>> getAllSensors() {
        return ResponseEntity.ok(sensorService.getAllSensors());
    }
    @GetMapping("/{id}")
    public ResponseEntity<Sensor> getMethodName(@PathVariable Long id) {
        return ResponseEntity.ok(sensorService.getSensorById(id));
    }
    
}
