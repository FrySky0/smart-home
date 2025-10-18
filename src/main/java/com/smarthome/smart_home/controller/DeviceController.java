package com.smarthome.smart_home.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.smarthome.smart_home.dto.CreateDeviceDTO;
import com.smarthome.smart_home.dto.DeviceDTO;
import com.smarthome.smart_home.dto.DeviceStatusUpdateDTO;
import com.smarthome.smart_home.model.Device;
import com.smarthome.smart_home.enums.DeviceStatus;
import com.smarthome.smart_home.enums.DeviceType;
import com.smarthome.smart_home.mappers.DeviceMapper;
import com.smarthome.smart_home.service.DeviceService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/devices")
public class DeviceController {
    private final DeviceService deviceService;
    private final DeviceMapper deviceMapper;

    public DeviceController(DeviceService deviceService, DeviceMapper deviceMapper) {
        this.deviceService = deviceService;
        this.deviceMapper = deviceMapper;
    }

    // Получить все устройства, с возможностью фильтрации по комнате, типу и статусу
    @GetMapping
    public ResponseEntity<List<DeviceDTO>> getAllDevices(
            @RequestParam(required = false) Long roomId,
            @RequestParam(required = false) DeviceType type,
            @RequestParam(required = false) DeviceStatus status) {
        List<DeviceDTO> deviceDTOs;
        if (roomId != null || type != null || status != null) {
            deviceDTOs = deviceService.getDevicesByFilters(roomId, type, status)
                    .stream()
                    .map(deviceMapper::toDTO)
                    .collect(Collectors.toList());
        } else {
            deviceDTOs = deviceService.getAllDevices().stream()
                    .map(deviceMapper::toDTO)
                    .collect(Collectors.toList());
        }
        return ResponseEntity.ok(deviceDTOs);
    }

    // Получить устройство по ID
    @GetMapping("/{id}")
    public ResponseEntity<DeviceDTO> getDeviceById(@PathVariable @NotNull Long id) {
        Device device = deviceService.getDeviceById(id);
        return ResponseEntity.ok(deviceMapper.toDTO(device));
    }

    // Создать новое устройство
    @PostMapping
    public ResponseEntity<DeviceDTO> createDevice(@Valid @RequestBody CreateDeviceDTO createDeviceDTO) {
        Device device = deviceMapper.toEntity(createDeviceDTO);
        Device savedDevice = deviceService.createDevice(device, createDeviceDTO.getRoomId());
        return ResponseEntity.status(HttpStatus.CREATED).body(deviceMapper.toDTO(savedDevice));
    }

    // Обновить устройство
    @PutMapping("/{id}")
    public ResponseEntity<DeviceDTO> updateDevice(@PathVariable @NotNull Long id,
            @Valid @RequestBody CreateDeviceDTO createDeviceDTO) {
        Device device = deviceMapper.toEntity(createDeviceDTO);
        Device updatedDevice = deviceService.updateDevice(id, device, createDeviceDTO.getRoomId());
        return ResponseEntity.ok(deviceMapper.toDTO(updatedDevice));
    }

    // Обновить только статус устройства
    @PatchMapping("/{id}")
    public ResponseEntity<DeviceDTO> patchDeviceStatus(@PathVariable @NotNull Long id,
            @Valid @RequestBody DeviceStatusUpdateDTO updateDTO) {
        return ResponseEntity.ok(deviceService.updateDeviceStatus(id, updateDTO));
    }

    // Удалить устройство
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDevice(@PathVariable @NotNull Long id) {
        deviceService.deleteDevice(id);
        return ResponseEntity.noContent().build();
    }
}
