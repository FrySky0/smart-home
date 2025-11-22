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

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
@Tag(name = "Sensors", description = "Управление сенсорами")
@Slf4j
public class SensorController {
    private final SensorService sensorService;
    private final SensorMapper sensorMapper;

    public SensorController(SensorService sensorService, SensorMapper sensorMapper) {
        this.sensorService = sensorService;
        this.sensorMapper = sensorMapper;
    }

    // Получить все сенсоры, с возможностью фильтрации по комнате и типу
    @GetMapping
    public ResponseEntity<Page<SensorDTO>> getAllSensors(
            @RequestParam(required = false) Long roomId,
            @RequestParam(required = false) SensorType type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection) {

        log.info(
                "Getting all sensors with filters - roomId: {}, type: {}, page: {}, size: {}, sortBy: {}, sortDirection: {}",
                roomId, type, page, size, sortBy, sortDirection);

        Sort sort = sortDirection.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<SensorDTO> sensorPage = sensorService.getSensorsByFilters(roomId, type, pageable)
                .map(sensorMapper::toDTO);

        log.info("Successfully retrieved {} sensors on page {} of {}",
                sensorPage.getNumberOfElements(), page, sensorPage.getTotalPages());

        return ResponseEntity.ok(sensorPage);
    }

    // Получить сенсор по ID
    @GetMapping("/{id}")
    public ResponseEntity<SensorDTO> getSensorById(@PathVariable @NotNull Long id) {
        log.info("Getting sensor by ID: {}", id);

        Sensor sensor = sensorService.getSensorById(id);
        SensorDTO sensorDTO = sensorMapper.toDTO(sensor);

        log.info("Successfully retrieved sensor with ID: {}", id);
        return ResponseEntity.ok(sensorDTO);
    }

    // Создать новый сенсор
    @PostMapping
    public ResponseEntity<SensorDTO> createSensor(@Valid @RequestBody CreateSensorDTO createSensorDTO) {
        log.info("Creating new sensor with name: {}, type: {}, roomId: {}",
                createSensorDTO.getName(), createSensorDTO.getType(), createSensorDTO.getRoomId());

        Sensor sensor = sensorMapper.toEntity(createSensorDTO);
        Sensor savedSensor = sensorService.createSensor(sensor, createSensorDTO.getRoomId());
        SensorDTO savedSensorDTO = sensorMapper.toDTO(savedSensor);

        log.info("Successfully created sensor with ID: {}", savedSensor.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(savedSensorDTO);
    }

    // Обновить сенсор
    @PutMapping("/{id}")
    public ResponseEntity<SensorDTO> updateSensor(@PathVariable @NotNull Long id,
            @Valid @RequestBody CreateSensorDTO createSensorDTO) {
        log.info("Updating sensor with ID: {}, new name: {}, new type: {}, new roomId: {}",
                id, createSensorDTO.getName(), createSensorDTO.getType(), createSensorDTO.getRoomId());

        Sensor sensor = sensorMapper.toEntity(createSensorDTO);
        Sensor updatedSensor = sensorService.updateSensor(id, sensor, createSensorDTO.getRoomId());
        SensorDTO updatedSensorDTO = sensorMapper.toDTO(updatedSensor);

        log.info("Successfully updated sensor with ID: {}", id);
        return ResponseEntity.ok(updatedSensorDTO);
    }

    // Удалить сенсор
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSensor(@PathVariable @NotNull Long id) {
        log.info("Deleting sensor with ID: {}", id);

        sensorService.deleteSensor(id);

        log.info("Successfully deleted sensor with ID: {}", id);
        return ResponseEntity.noContent().build();
    }
}
