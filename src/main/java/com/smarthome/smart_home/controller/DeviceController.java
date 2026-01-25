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

import com.smarthome.smart_home.dto.create.DeviceCreateDTO;
import com.smarthome.smart_home.dto.response.DeviceResponseDTO;
import com.smarthome.smart_home.dto.update.patch.DevicePatchDTO;
import com.smarthome.smart_home.dto.update.put.DevicePutDTO;
import com.smarthome.smart_home.enums.DeviceStatus;
import com.smarthome.smart_home.enums.DeviceType;
import com.smarthome.smart_home.mappers.DeviceMapper;
import com.smarthome.smart_home.model.Device;
import com.smarthome.smart_home.service.DeviceService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
    @Operation(summary = "Получить все устройства с возможностью фильтрации", description = "Получить список всех устройств с возможностью фильтрации по имени, комнате, типу и статусу устройства.")
    @GetMapping
    public ResponseEntity<Page<DeviceResponseDTO>> getAllDevices(
            @Parameter(description="Поиск по названию (частичное совпадение)") @RequestParam(required = false) String name,
            @Parameter(description="Поиск по ID комнаты, в которой стоит этот девайс") @RequestParam(required = false) Long roomId,
            @Parameter(description="Поиск по типу девайса") @RequestParam(required = false) DeviceType type,
            @Parameter(description="Поиск по статусу") @RequestParam(required = false) DeviceStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection) {

        log.info(
                "Getting all devices with filters - name: {}, roomId: {}, type: {}, status: {}, page: {}, size: {}, sortBy: {}, sortDirection: {}",
                name, roomId, type, status, page, size, sortBy, sortDirection);

        Sort sort = sortDirection.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<DeviceResponseDTO> devicePage = deviceService.getDevicesByFilters(name,roomId, type, status, pageable)
                .map(deviceMapper::toResponseDTO);

        log.info("Successfully retrieved {} devices on page {}", devicePage.getNumberOfElements(), page);
        return ResponseEntity.ok(devicePage);
    }

    // Получить устройство по ID
    @Operation(summary = "Получить устройство по ID", description = "Получить детали устройства по его уникальному идентификатору.")
    @GetMapping("/{id}")
    public ResponseEntity<DeviceResponseDTO> getDeviceById(@PathVariable @NotNull Long id) {
        log.info("Getting device by ID: {}", id);

        Device device = deviceService.getDeviceById(id);
        log.info("Device found with ID: {}", id);
        return ResponseEntity.ok(deviceMapper.toResponseDTO(device));
    }

    // Создать новое устройство
    @Operation(summary = "Создать новое устройство", description = "Создать новое устройство с указанными параметрами.")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DeviceResponseDTO> createDevice(@Valid @RequestBody DeviceCreateDTO deviceCreateDTO) {
        log.info("Creating new device with name: {}, type: {}, roomId: {}",
                deviceCreateDTO.getName(), deviceCreateDTO.getType(), deviceCreateDTO.getRoomId());

        Device device = deviceService.createDevice(deviceCreateDTO);
        DeviceResponseDTO deviceDTO = deviceMapper.toResponseDTO(device);

        log.info("Successfully created device with ID: {}", device.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(deviceDTO);
    }

    @Operation(summary = "Полностью обновить устройство", description = "Обновить все поля существующего устройства по его ID.")
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<DeviceResponseDTO> updateDeviceFull(@PathVariable @NotNull Long id, @Valid @RequestBody DevicePutDTO devicePutDTO) {
        log.info("Fully updating device with ID: {}", id);

        Device device = deviceService.updateFull(id, devicePutDTO);
        DeviceResponseDTO deviceDTO = deviceMapper.toResponseDTO(device);
        log.info("The device with ID {} has been successfully fully updated", id);
        return ResponseEntity.ok(deviceDTO);
    }

    @Operation(summary = "Частично обновить устройство", description = "Обновить определённые поля существующего устройства по его ID.")
    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<DeviceResponseDTO> updateDevicePartially(@PathVariable @NotNull Long id,
            @Valid @RequestBody @NotNull DevicePatchDTO devicePatchDTO) {
        log.info("Partially updating device with ID: {}", id);

        Device updatedDevice = deviceService.updatePartially(id, devicePatchDTO);
        DeviceResponseDTO deviceDTO = deviceMapper.toResponseDTO(updatedDevice);

        log.info("The device with ID {} has been successfully partially updated", id);
        return ResponseEntity.ok(deviceDTO);
    }

    @Operation(summary = "Удалить устройство", description = "Удалить устройство по его уникальному идентификатору.")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<Void> deleteDevice(@PathVariable @NotNull Long id) {
        log.info("Deleting device with ID: {}", id);

        deviceService.deleteDevice(id);

        log.info("Successfully deleted device with ID: {}", id);
        return ResponseEntity.noContent().build();
    }
}
