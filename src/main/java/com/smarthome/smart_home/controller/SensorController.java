package com.smarthome.smart_home.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.smarthome.smart_home.model.Sensor;
import com.smarthome.smart_home.dto.SensorDTO;
import com.smarthome.smart_home.enums.SensorType;
import com.smarthome.smart_home.mappers.SensorMapper;
import com.smarthome.smart_home.service.SensorService;

import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/sensors")
public class SensorController {
    private final SensorService sensorService;
    private final SensorMapper sensorMapper;

    public SensorController(SensorService sensorService, SensorMapper sensorMapper) {
        this.sensorService = sensorService;
        this.sensorMapper = sensorMapper;
    }

    // Получить все сенсоры, с возможностью фильтрации по комнате и типу
    @GetMapping
    public ResponseEntity<List<SensorDTO>> getAllSensors(
            @RequestParam(required = false) Long roomId,
            @RequestParam(required = false) SensorType type) {

        List<SensorDTO> sensorDTOs;
        if (roomId != null || type != null) {
            sensorDTOs = sensorService.getSensorsByFilters(roomId, type).stream()
                    .map(sensorMapper::toDTO)
                    .collect(Collectors.toList());
        } else {
            sensorDTOs = sensorService.getAllSensors().stream()
                    .map(sensorMapper::toDTO)
                    .collect(Collectors.toList());
        }
        return ResponseEntity.ok(sensorDTOs);
    }

    // Получить сенсор по ID
    @GetMapping("/{id}")
    public ResponseEntity<SensorDTO> getSensorById(@PathVariable @NotNull Long id) {
        Sensor sensor = sensorService.getSensorById(id);
        return ResponseEntity.ok(sensorMapper.toDTO(sensor));
    }

}
