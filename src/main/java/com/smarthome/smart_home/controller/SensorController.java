package com.smarthome.smart_home.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.smarthome.smart_home.model.Sensor;
import com.smarthome.smart_home.dto.CreateSensorDTO;
import com.smarthome.smart_home.dto.SensorDTO;
import com.smarthome.smart_home.enums.SensorType;
import com.smarthome.smart_home.mappers.SensorMapper;
import com.smarthome.smart_home.service.SensorService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

    // Создать новый сенсор
    @PostMapping
    public ResponseEntity<SensorDTO> createSensor(@Valid @RequestBody CreateSensorDTO createSensorDTO) {
        Sensor sensor = sensorMapper.toEntity(createSensorDTO);
        Sensor savedSensor = sensorService.createSensor(sensor, createSensorDTO.getRoomId());
        return ResponseEntity.status(HttpStatus.CREATED).body(sensorMapper.toDTO(savedSensor));
    }

    // Обновить сенсор
    @PutMapping("/{id}")
    public ResponseEntity<SensorDTO> updateSensor(@PathVariable @NotNull Long id,
            @Valid @RequestBody CreateSensorDTO createSensorDTO) {
        Sensor sensor = sensorMapper.toEntity(createSensorDTO);
        Sensor updatedSensor = sensorService.updateSensor(id, sensor, createSensorDTO.getRoomId());
        return ResponseEntity.ok(sensorMapper.toDTO(updatedSensor));
    }

    // Удалить сенсор
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSensor(@PathVariable @NotNull Long id) {
        sensorService.deleteSensor(id);
        return ResponseEntity.noContent().build();
    }
}
