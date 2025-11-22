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
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/devices")
@Tag(name = "Devices", description = "Управление устройствами")
@Slf4j
public class DeviceController {
    private final DeviceService deviceService;
    private final DeviceMapper deviceMapper;

    public DeviceController(DeviceService deviceService, DeviceMapper deviceMapper) {
        this.deviceService = deviceService;
        this.deviceMapper = deviceMapper;
    }

    // Получить все устройства, с возможностью фильтрации по комнате, типу и статусу
    @GetMapping
    public ResponseEntity<Page<DeviceDTO>> getAllDevices(
            @RequestParam(required = false) Long roomId,
            @RequestParam(required = false) DeviceType type,
            @RequestParam(required = false) DeviceStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection) {

        log.info(
                "Getting all devices with filters - roomId: {}, type: {}, status: {}, page: {}, size: {}, sortBy: {}, sortDirection: {}",
                roomId, type, status, page, size, sortBy, sortDirection);

        Sort sort = sortDirection.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<DeviceDTO> devicePage = deviceService.getDevicesByFilters(roomId, type, status, pageable)
                .map(deviceMapper::toDTO);

        log.info("Successfully retrieved {} devices on page {}", devicePage.getNumberOfElements(), page);
        return ResponseEntity.ok(devicePage);
    }

    // Получить устройство по ID
    @GetMapping("/{id}")
    public ResponseEntity<DeviceDTO> getDeviceById(@PathVariable @NotNull Long id) {
        log.info("Getting device by ID: {}", id);

        Device device = deviceService.getDeviceById(id);
        DeviceDTO deviceDTO = deviceMapper.toDTO(device);

        log.info("Successfully retrieved device with ID: {}", id);
        return ResponseEntity.ok(deviceDTO);
    }

    // Создать новое устройство
    @PostMapping
    public ResponseEntity<DeviceDTO> createDevice(@Valid @RequestBody CreateDeviceDTO createDeviceDTO) {
        log.info("Creating new device with name: {}, type: {}, roomId: {}",
                createDeviceDTO.getName(), createDeviceDTO.getType(), createDeviceDTO.getRoomId());

        Device device = deviceMapper.toEntity(createDeviceDTO);
        Device savedDevice = deviceService.createDevice(device, createDeviceDTO.getRoomId());
        DeviceDTO savedDeviceDTO = deviceMapper.toDTO(savedDevice);

        log.info("Successfully created device with ID: {}", savedDevice.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(savedDeviceDTO);
    }

    @PutMapping("/{id}/setValue")
    public Device putMethodName(@PathVariable @NotNull Long id, @RequestBody Double value) {
        log.info("Setting value for device ID: {} to value: {}", id, value);

        Device device = deviceService.setValue(deviceService.getDeviceById(id), value);

        log.info("Successfully set value for device ID: {} to value: {}", id, value);
        return device;
    }

    // Обновить устройство
    @PutMapping("/{id}")
    public ResponseEntity<DeviceDTO> updateDevice(@PathVariable @NotNull Long id,
            @Valid @RequestBody CreateDeviceDTO createDeviceDTO) {
        log.info("Updating device with ID: {}, new name: {}, type: {}, roomId: {}",
                id, createDeviceDTO.getName(), createDeviceDTO.getType(), createDeviceDTO.getRoomId());

        Device device = deviceMapper.toEntity(createDeviceDTO);
        Device updatedDevice = deviceService.updateDevice(id, device, createDeviceDTO.getRoomId());
        DeviceDTO updatedDeviceDTO = deviceMapper.toDTO(updatedDevice);

        log.info("Successfully updated device with ID: {}", id);
        return ResponseEntity.ok(updatedDeviceDTO);
    }

    // Обновить только статус устройства
    @PatchMapping("/{id}")
    public ResponseEntity<DeviceDTO> patchDeviceStatus(@PathVariable @NotNull Long id,
            @Valid @RequestBody DeviceStatusUpdateDTO updateDTO) {
        log.info("Updating status for device ID: {} to status: {}", id, updateDTO.getStatus());

        DeviceDTO updatedDevice = deviceService.updateDeviceStatus(id, updateDTO);

        log.info("Successfully updated status for device ID: {} to status: {}", id, updateDTO.getStatus());
        return ResponseEntity.ok(updatedDevice);
    }

    // Удалить устройство
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDevice(@PathVariable @NotNull Long id) {
        log.info("Deleting device with ID: {}", id);

        deviceService.deleteDevice(id);

        log.info("Successfully deleted device with ID: {}", id);
        return ResponseEntity.noContent().build();
    }
}
