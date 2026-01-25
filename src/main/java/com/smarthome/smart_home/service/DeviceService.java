package com.smarthome.smart_home.service;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.smarthome.smart_home.dto.CreateDeviceDTO;
import com.smarthome.smart_home.dto.DeviceUpdateDTO;
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
    public Device getDeviceByUuid(UUID uuid){
        log.debug("Fetching device by uuid: {}", uuid);
        Device device = deviceRepository.findByUuid(uuid)
                .orElseThrow(() -> {
                    log.error("Device not found with id: {}", uuid);
                    return new ResourceNotFoundException("Device not found with id: " + uuid);
                });
        log.info("Successfully fetched device with id: {}", uuid);
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
    @Transactional
    public Device createDevice(CreateDeviceDTO createDeviceDTO) {
        log.debug("Creating new device '{}' for room ID: {}",createDeviceDTO.getName(), createDeviceDTO.getRoomId());
        Device device = new Device();
        Room room = roomService.getRoomById(createDeviceDTO.getRoomId());
        device.setName(createDeviceDTO.getName());
        device.setType(createDeviceDTO.getType());
        device.setRoom(room);

        if (createDeviceDTO.getType().hasValue()){
            if (createDeviceDTO.getValue() != null) {
                device.setValue(createDeviceDTO.getValue());
            } else {
                device.setValue(0.0);
            }
        }
        else if (createDeviceDTO.getValue() != null) {
            log.error("Device type {} does not support values, but a value was provided", createDeviceDTO.getType());
            throw new ValidationException("Device type " + createDeviceDTO.getType() + " does not support values");
        }

        if (createDeviceDTO.getStatus() != null) {
            device.setStatus(createDeviceDTO.getStatus());
        } else {
            device.setStatus(DeviceStatus.OFF);
        }
        
        Device savedDevice = deviceRepository.save(device);
        log.info("Successfully created device with ID: {} - {} in room: {}",
                savedDevice.getId(), savedDevice.getName(), room.getName());
        return savedDevice;
    }
    @Transactional
    public Device updateFull(Long id, DeviceUpdateDTO deviceUpdateDTO) {
        log.debug("Fully updating device with id: {}", id);
        Device existingDevice = getDeviceById(id);
        Room room = roomService.getRoomById(deviceUpdateDTO.getRoomId());

        existingDevice.setName(deviceUpdateDTO.getName());
        existingDevice.setStatus(deviceUpdateDTO.getStatus());
        existingDevice.setRoom(room);
        if (existingDevice.getType().hasValue()){
            if (deviceUpdateDTO.getValue() == null) {
                log.error("Device type {} requires a value, but none was provided", existingDevice.getType());
                throw new ValidationException("Device type " + existingDevice.getType() + " requires a value");
            }
            else{
                existingDevice.setValue(deviceUpdateDTO.getValue());
            }
        }
        Device updatedDevice = deviceRepository.save(existingDevice);
        log.info("Successfully fully updated device with id: {}", id);
        return updatedDevice;
    }
    @Transactional
    public Device updatePartially(Long id, DeviceUpdateDTO deviceUpdateDTO) {
        log.debug("Partially updating device with id: {}", id);
        Device existingDevice = getDeviceById(id);
        if (deviceUpdateDTO.getName() != null) {
            existingDevice.setName(deviceUpdateDTO.getName());
        }
        if (deviceUpdateDTO.getStatus() != null) {
            existingDevice.setStatus(deviceUpdateDTO.getStatus());
        }
        if (deviceUpdateDTO.getRoomId() != null) {
            Room room = roomService.getRoomById(deviceUpdateDTO.getRoomId());
            existingDevice.setRoom(room);
        }
        if (deviceUpdateDTO.getValue() != null) {
            if (existingDevice.getType().hasValue()){
                existingDevice.setValue(deviceUpdateDTO.getValue());
            } else {
                log.error("Cannot set value for device type {} which does not support values", existingDevice.getType());
                throw new ValidationException("Cannot set value for device type " + existingDevice.getType() + " which does not support values");
            }
        }
        Device updatedDevice = deviceRepository.save(existingDevice);
        log.info("Successfully partially updated device with id: {}", id);
        return updatedDevice;
    }
    @Transactional
    public void deleteDevice(Long id) {
        log.debug("Deleting device with id: {}", id);
        if (!deviceRepository.existsById(id)) {
            log.error("Device not found for deletion with id: {}", id);
            throw new ResourceNotFoundException("Device not found with id: " + id);
        }
        deviceRepository.deleteById(id);
        log.info("Successfully deleted device with id: {}", id);
    }

    public Device setValue(Device device, Double value) {
        log.debug("Setting value for device id: {} to {}", device.getId(), value);
        device.setValue(value);
        Device savedDevice = deviceRepository.save(device);
        log.info("Successfully set value for device id: {} to {}", device.getId(), value);
        return savedDevice;
    }
}
