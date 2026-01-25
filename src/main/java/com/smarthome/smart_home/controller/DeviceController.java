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

import com.smarthome.smart_home.dto.CreateDeviceDTO;
import com.smarthome.smart_home.dto.DeviceDTO;
import com.smarthome.smart_home.dto.DeviceUpdateDTO;
import com.smarthome.smart_home.enums.DeviceStatus;
import com.smarthome.smart_home.enums.DeviceType;
import com.smarthome.smart_home.mappers.DeviceMapper;
import com.smarthome.smart_home.model.Device;
import com.smarthome.smart_home.service.DeviceService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;

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
        log.info("Device found with ID: {}", id);
        return ResponseEntity.ok(deviceMapper.toDTO(device));
    }

    // Создать новое устройство
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DeviceDTO> createDevice(@Valid @RequestBody CreateDeviceDTO createDeviceDTO) {
        log.info("Creating new device with name: {}, type: {}, roomId: {}",
                createDeviceDTO.getName(), createDeviceDTO.getType(), createDeviceDTO.getRoomId());

        Device device = deviceService.createDevice(createDeviceDTO);
        DeviceDTO deviceDTO = deviceMapper.toDTO(device);

        log.info("Successfully created device with ID: {}", device.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(deviceDTO);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<DeviceDTO> updateDeviceFull(@PathVariable @NotNull Long id, @Valid @RequestBody DeviceUpdateDTO deviceUpdateDTO) {
        log.info("Fully updating device with ID: {}", id);

        Device device = deviceService.updateFull(id, deviceUpdateDTO);
        DeviceDTO deviceDTO = deviceMapper.toDTO(device);
        log.info("The device with ID {} has been successfully fully updated", id);
        return ResponseEntity.ok(deviceDTO);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<DeviceDTO> updateDevicePartially(@PathVariable @NotNull Long id,
            @Valid @RequestBody @NotNull DeviceUpdateDTO deviceUpdateDTO) {
        log.info("Partially updating device with ID: {}", id);

        Device updatedDevice = deviceService.updatePartially(id, deviceUpdateDTO);
        DeviceDTO deviceDTO = deviceMapper.toDTO(updatedDevice);

        log.info("The device with ID {} has been successfully partially updated", id);
        return ResponseEntity.ok(deviceDTO);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<Void> deleteDevice(@PathVariable @NotNull Long id) {
        log.info("Deleting device with ID: {}", id);

        deviceService.deleteDevice(id);

        log.info("Successfully deleted device with ID: {}", id);
        return ResponseEntity.noContent().build();
    }
}
