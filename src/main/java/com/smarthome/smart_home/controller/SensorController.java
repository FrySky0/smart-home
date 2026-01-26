package com.smarthome.smart_home.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smarthome.smart_home.dto.create.SensorCreateDTO;
import com.smarthome.smart_home.dto.response.SensorResponseDTO;
import com.smarthome.smart_home.dto.update.patch.SensorPatchDTO;
import com.smarthome.smart_home.dto.update.put.SensorPutDTO;
import com.smarthome.smart_home.enums.SensorType;
import com.smarthome.smart_home.enums.activitylog.ComponentName;
import com.smarthome.smart_home.enums.activitylog.LogAction;
import com.smarthome.smart_home.mappers.SensorMapper;
import com.smarthome.smart_home.model.Sensor;
import com.smarthome.smart_home.service.LogService;
import com.smarthome.smart_home.service.SensorService;
import com.smarthome.smart_home.service.TelegramService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/sensors")
@Tag(name = "Sensors", description = "Управление сенсорами")
@Slf4j
public class SensorController {
    private final SensorService sensorService;
    private final SensorMapper sensorMapper;
    private final TelegramService telegramService;
    private final LogService logService;
    private final ObjectMapper objectMapper;

    public SensorController(SensorService sensorService, SensorMapper sensorMapper, TelegramService telegramService, LogService logService, ObjectMapper objectMapper) {
        this.sensorService = sensorService;
        this.sensorMapper = sensorMapper;
        this.telegramService = telegramService;
        this.logService = logService;
        this.objectMapper = objectMapper;
    }

    // Получить все сенсоры, с возможностью фильтрации по комнате и типу
    @Operation(summary = "Получить все сенсоры с возможностью фильтрации", description = "Получить список всех сенсоров с возможностью фильтрации по комнате и типу сенсора.")
    @GetMapping
    @PreAuthorize("hasAuthority('sensor:read')")
    public ResponseEntity<Page<SensorResponseDTO>> getAllSensors(
            @Parameter(description="Поиск по названию (частичное совпадение)") @RequestParam(required = false) String name,
            @Parameter(description="Поиск по ID комнаты, в которой стоит этот сенсор") @RequestParam(required = false) Long roomId,
            @Parameter(description="Поиск по типу сенсора") @RequestParam(required = false) SensorType type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection) {

        log.info(
                "Getting all sensors with filters - name: {}, roomId: {}, type: {}, page: {}, size: {}, sortBy: {}, sortDirection: {}",
                name, roomId, type, page, size, sortBy, sortDirection);

        Sort sort = sortDirection.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<SensorResponseDTO> sensorPage = sensorService.getSensorsByFilters(name, roomId, type, pageable)
                .map(sensorMapper::toDTO);

        log.info("Successfully retrieved {} sensors on page {} of {}",
                sensorPage.getNumberOfElements(), page, sensorPage.getTotalPages());

        return ResponseEntity.ok(sensorPage);
    }

    // Получить сенсор по ID
    @Operation(summary = "Получить сенсор по ID", description = "Получить детали сенсора по его уникальному идентификатору.")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('sensor:read')")
    public ResponseEntity<SensorResponseDTO> getSensorById(@PathVariable @NotNull Long id) {
        log.info("Getting sensor by ID: {}", id);

        Sensor sensor = sensorService.getSensorById(id);
        log.info("Sensor found with ID: {}", id);
        return ResponseEntity.ok(sensorMapper.toDTO(sensor));
    }

    // Создать новый сенсор
    @Operation(summary = "Создать новый сенсор", description = "Создать новый сенсор с указанными параметрами.")
    @PostMapping
    @PreAuthorize("hasAuthority('sensor:manage')")
    public ResponseEntity<SensorResponseDTO> createSensor(@Valid @RequestBody SensorCreateDTO sensorCreateDTO) {
        log.info("Creating new sensor with name: {}, type: {}, roomId: {}, value: {}",
                sensorCreateDTO.getName(), sensorCreateDTO.getType(), sensorCreateDTO.getRoomId(), sensorCreateDTO.getValue());

        Sensor sensor = sensorService.createSensor(sensorCreateDTO);
        SensorResponseDTO sensorDTO = sensorMapper.toDTO(sensor);

        log.info("Successfully created sensor with ID: {} and UUID: {}", sensor.getId(), sensor.getUuid());
        
        telegramService.sendLog("New sensor created: " + sensor.getName() + " (ID: " + sensor.getId() + ")");
        
        logService.logEntity(ComponentName.Sensor, LogAction.CREATED, sensorDTO);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(sensorDTO);
    }

    // Обновить сенсор
    @Operation(summary = "Полностью обновить сенсор", description = "Обновить все поля существующего сенсора по его ID.")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('sensor:control')")
    public ResponseEntity<SensorResponseDTO> updateSensorFull(@PathVariable @NotNull Long id,
            @Valid @RequestBody SensorPutDTO sensorPutDTO) {
        log.info("Fully updating sensor ID: {}", id);
        Sensor sensor = sensorService.updateFull(id, sensorPutDTO);
        SensorResponseDTO sensorDTO = sensorMapper.toDTO(sensor);

        log.info("The sensor with ID {} has been successfully fully updated", id);
        telegramService.sendLog("Sensor " + sensor.getName() + " (ID: " + sensor.getId() + ") fully updated");
        logService.logEntity(ComponentName.Sensor, LogAction.UPDATED, sensorDTO);
        return ResponseEntity.ok(sensorDTO);
    }
    @Operation(summary = "Частично обновить сенсор", description = "Обновить определённые поля существующего сенсора по его ID.")
    @PatchMapping("/{id}")
    @PreAuthorize("hasAuthority('sensor:control')")
    public ResponseEntity<SensorResponseDTO> updateSensorPartially(@PathVariable @NotNull Long id,
            @Valid @RequestBody @NotNull SensorPatchDTO sensorPatchDTO) {
        log.info("Partially updating sensor with ID: {}", id);

        Sensor updatedSensor = sensorService.updatePartially(id, sensorPatchDTO);
        SensorResponseDTO sensorDTO = sensorMapper.toDTO(updatedSensor);

        log.info("The sensor with ID {} has been successfully partially updated", id);
        telegramService.sendLog("Sensor " + updatedSensor.getName() + " (ID: " + updatedSensor.getId() + ") partially updated");
        logService.logEntity(ComponentName.Sensor, LogAction.UPDATED, sensorDTO);
        return ResponseEntity.ok(sensorDTO);
    }

    // Удалить сенсор
    @Operation(summary = "Удалить сенсор", description = "Удалить сенсор по его уникальному идентификатору.")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('sensor:manage')")
    public ResponseEntity<Void> deleteSensor(@PathVariable @NotNull Long id) {
        log.info("Deleting sensor with ID: {}", id);

        sensorService.deleteSensor(id);

        log.info("Successfully deleted sensor with ID: {}", id);
        telegramService.sendLog("Sensor with ID " + id + " has been deleted");
        logService.log(ComponentName.Sensor, LogAction.DELETED, "ID:"+id);
        return ResponseEntity.noContent().build();
    }
}
