package com.smarthome.smart_home.service;

import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.smarthome.smart_home.dto.DeviceDTO;
import com.smarthome.smart_home.dto.DeviceStatusUpdateDTO;
import com.smarthome.smart_home.enums.DeviceStatus;
import com.smarthome.smart_home.enums.DeviceType;
import com.smarthome.smart_home.exception.ResourceNotFoundException;
import com.smarthome.smart_home.mappers.DeviceMapper;
import com.smarthome.smart_home.model.Device;
import com.smarthome.smart_home.model.Room;
import com.smarthome.smart_home.repository.DeviceRepository;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeviceService {

    private final DeviceRepository deviceRepository;
    private final DeviceMapper deviceMapper;
    private final RoomService roomService;

    public List<Device> getAllDevices() {
        log.debug("Fetching all devices");
        List<Device> devices = deviceRepository.findAll();
        log.info("Successfully fetched {} devices", devices.size());
        return devices;
    }

    public Page<Device> getDevicesByFilters(Long roomId, DeviceType type, DeviceStatus status, Pageable pageable) {
        log.debug("Fetching devices with filters - roomId: {}, type: {}, status: {}, pageable: {}",
                roomId, type, status, pageable);
        Page<Device> devices = deviceRepository.findByFilters(roomId, type, status, pageable);
        log.info("Successfully fetched {} devices with applied filters", devices.getTotalElements());
        return devices;
    }

    public Device getDeviceById(Long id) {
        log.debug("Fetching device by id: {}", id);
        Device device = deviceRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Device not found with id: {}", id);
                    return new ResourceNotFoundException("Device not found with id: " + id);
                });
        log.info("Successfully fetched device with id: {}", id);
        return device;
    }

    public Device turnOff(Device device) {
        log.debug("Turning off device with id: {}", device.getId());
        device.setStatus(DeviceStatus.OFF);
        Device savedDevice = deviceRepository.save(device);
        log.info("Successfully turned off device with id: {}", device.getId());
        return savedDevice;
    }

    public Device turnOn(Device device) {
        log.debug("Turning on device with id: {}", device.getId());
        device.setStatus(DeviceStatus.ON);
        Device savedDevice = deviceRepository.save(device);
        log.info("Successfully turned on device with id: {}", device.getId());
        return savedDevice;
    }

    public Device createDevice(Device device, Long roomId) {
        log.debug("Creating new device for room id: {}", roomId);
        Room room = roomService.getRoomById(roomId);
        device.setRoom(room);
        device.setStatus(DeviceStatus.OFF);
        Device savedDevice = deviceRepository.save(device);
        log.info("Successfully created device with id: {} for room id: {}", savedDevice.getId(), roomId);
        return savedDevice;
    }

    public Device updateDevice(Long id, Device deviceDetails, Long roomId) {
        log.debug("Updating device with id: {}", id);
        Device existingDevice = getDeviceById(id);
        Room room = roomService.getRoomById(roomId);

        existingDevice.setName(deviceDetails.getName());
        existingDevice.setType(deviceDetails.getType());
        existingDevice.setRoom(room);

        Device updatedDevice = deviceRepository.save(existingDevice);
        log.info("Successfully updated device with id: {}", id);
        return updatedDevice;
    }

    public void deleteDevice(Long id) {
        log.debug("Deleting device with id: {}", id);
        if (!deviceRepository.existsById(id)) {
            log.error("Device not found for deletion with id: {}", id);
            throw new ResourceNotFoundException("Device not found with id: " + id);
        }
        deviceRepository.deleteById(id);
        log.info("Successfully deleted device with id: {}", id);
    }

    public DeviceDTO updateDeviceStatus(Long id, DeviceStatusUpdateDTO updateDTO) {
        log.debug("Updating device status for id: {} with status: {}", id,
                updateDTO != null ? updateDTO.getStatus() : "null");

        if (updateDTO == null || updateDTO.getStatus() == null) {
            log.error("Invalid status update request for device id: {}. Status is required", id);
            throw new ValidationException("Status is required");
        }

        Device device = getDeviceById(id);
        device.setStatus(updateDTO.getStatus());
        deviceRepository.save(device);

        DeviceDTO deviceDTO = deviceMapper.toDTO(device);
        log.info("Successfully updated device status for id: {} to {}", id, updateDTO.getStatus());
        return deviceDTO;
    }

    public Device setValue(Device device, Double value) {
        log.debug("Setting value for device id: {} to {}", device.getId(), value);
        device.setValue(value);
        Device savedDevice = deviceRepository.save(device);
        log.info("Successfully set value for device id: {} to {}", device.getId(), value);
        return savedDevice;
    }

    public List<Device> getDevicesByRoomId(Long id) {
        log.debug("Fetching devices by room id: {}", id);
        List<Device> devices = deviceRepository.findByRoomId(id);
        log.info("Successfully fetched {} devices for room id: {}", devices.size(), id);
        return devices;
    }

    public List<Device> getDevicesByType(DeviceType type) {
        log.debug("Fetching devices by type: {}", type);
        List<Device> devices = deviceRepository.findByType(type);
        log.info("Successfully fetched {} devices of type: {}", devices.size(), type);
        return devices;
    }

    public List<Device> getDevicesByStatus(DeviceStatus status) {
        log.debug("Fetching devices by status: {}", status);
        List<Device> devices = deviceRepository.findByStatus(status);
        log.info("Successfully fetched {} devices with status: {}", devices.size(), status);
        return devices;
    }

    public List<Device> getDevicesByFilters(Long roomId, DeviceType type, DeviceStatus status) {
        log.debug("Fetching devices with filters - roomId: {}, type: {}, status: {}", roomId, type, status);
        List<Device> devices = deviceRepository.findByFilters(roomId, type, status);
        log.info("Successfully fetched {} devices with applied filters", devices.size());
        return devices;
    }

    public List<Device> getDevicesByRoomName(String roomName) {
        log.debug("Fetching devices by room name: {}", roomName);
        if (roomName == null || roomName.isEmpty()) {
            log.error("Room name is required but was: {}", roomName);
            throw new ValidationException("Room name is required");
        }
        List<Device> devices = deviceRepository.findByRoomName(roomName);
        log.info("Successfully fetched {} devices for room name: {}", devices.size(), roomName);
        return devices;
    }
}
