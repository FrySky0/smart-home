package com.smarthome.smart_home.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.smarthome.smart_home.dto.DeviceDTO;
import com.smarthome.smart_home.dto.DeviceStatusUpdateDTO;
import com.smarthome.smart_home.enums.DeviceStatus;
import com.smarthome.smart_home.enums.DeviceType;
import com.smarthome.smart_home.exception.ResourceNotFoundException;
import com.smarthome.smart_home.mappers.DeviceMapper;
import com.smarthome.smart_home.model.Device;
import com.smarthome.smart_home.repository.DeviceRepository;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DeviceService {

    private final DeviceRepository deviceRepository;
    private final DeviceMapper deviceMapper;

    public List<Device> getAllDevices() {
        return deviceRepository.findAll();
    }

    public Device getDeviceById(Long id) {
        return deviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Device not found with id: " + id));
    }

    public DeviceDTO updateDeviceStatus(Long id, DeviceStatusUpdateDTO updateDTO) {
        if (updateDTO.getStatus() == null || updateDTO == null) {
            throw new ValidationException("Status is required");
        }

        Device device = getDeviceById(id);
        device.setStatus(updateDTO.getStatus());
        deviceRepository.save(device);
        return deviceMapper.toDTO(device);
    }

    public List<Device> getDevicesByRoomId(Long id) {
        return deviceRepository.findByRoomId(id);
    }

    public List<Device> getDevicesByType(DeviceType type) {
        return deviceRepository.findByType(type);
    }

    public List<Device> getDevicesByStatus(DeviceStatus status) {
        return deviceRepository.findByStatus(status);
    }

    public List<Device> getDevicesByFilters(Long roomId, DeviceType type, DeviceStatus status) {
        return deviceRepository.findByFilters(roomId, type, status);
    }

    public List<Device> getDevicesByRoomName(String roomName) {
        if (roomName == null || roomName.isEmpty()) {
            throw new ValidationException("Room name is required");
        }
        return deviceRepository.findByRoomName(roomName);
    }
}
