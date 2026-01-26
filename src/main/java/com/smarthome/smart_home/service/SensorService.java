package com.smarthome.smart_home.service;

import java.util.UUID;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.smarthome.smart_home.dto.create.SensorCreateDTO;
import com.smarthome.smart_home.dto.update.patch.SensorPatchDTO;
import com.smarthome.smart_home.dto.update.put.SensorPutDTO;
import com.smarthome.smart_home.enums.SensorType;
import com.smarthome.smart_home.events.SensorUpdatedEvent;
import com.smarthome.smart_home.exception.ResourceNotFoundException;
import com.smarthome.smart_home.model.Room;
import com.smarthome.smart_home.model.Sensor;
import com.smarthome.smart_home.repository.SensorRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class SensorService {
    private final SensorRepository sensorRepository;
    private final RoomService roomService;
    private final ApplicationEventPublisher eventPublisher;

    public Sensor getSensorById(Long id) {
        log.debug("Looking for sensor with ID: {}", id);
        Sensor sensor = sensorRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Sensor not found with id: {}", id);
                    return new ResourceNotFoundException("Sensor not found with id: " + id);
                });
        log.info("Successfully found sensor with ID: {} - {}", id, sensor.getName());
        return sensor;
    }

    public Sensor getSensorByUuid(UUID uuid) {
        log.debug("Looking for sensor with UUID: {}", uuid);
        Sensor sensor = sensorRepository.findByUuid(uuid)
                .orElseThrow(() -> {
                    log.error("Sensor not found with UUID: {}", uuid);
                    return new ResourceNotFoundException("Sensor not found with UUID: " + uuid);
                });
        log.info("Successfully found sensor with UUID: {} - {}", uuid, sensor.getName());
        return sensor;
    }

    @Transactional
    public Sensor createSensor(SensorCreateDTO createSensorDTO) {
        log.debug("Creating new sensor '{}' for room ID: {}", createSensorDTO.getName(), createSensorDTO.getRoomId());
        Sensor sensor = new Sensor();
        Room room = roomService.getRoomById(createSensorDTO.getRoomId());
        sensor.setName(createSensorDTO.getName());
        sensor.setType(createSensorDTO.getType());
        sensor.setRoom(room);
        if (createSensorDTO.getValue() != null) {
            sensor.setValue(createSensorDTO.getValue());
        } else {
            sensor.setValue(0.0);
        }
        Sensor savedSensor = sensorRepository.save(sensor);
        eventPublisher.publishEvent(new SensorUpdatedEvent(sensor));
        log.info("Successfully created sensor with ID: {} - {} in room: {}",
                savedSensor.getId(), savedSensor.getName(), room.getName());
        return savedSensor;
    }
    @Transactional
    public Sensor updateFull(Long id, SensorPutDTO sensorPutDTO){
        log.debug("Fully updating sensor with id: {}", id);
        Sensor existingSensor = getSensorById(id);
        Room room = roomService.getRoomById(sensorPutDTO.getRoomId());
        existingSensor.setName(sensorPutDTO.getName());
        existingSensor.setValue(sensorPutDTO.getValue());
        existingSensor.setRoom(room);
        Sensor updatedSensor = sensorRepository.save(existingSensor);
        eventPublisher.publishEvent(new SensorUpdatedEvent(updatedSensor));
        log.debug("Successfully fully updated sensor with id: {}", id);
        return updatedSensor;
    }
    @Transactional
    public Sensor updatePartially(Long id, SensorPatchDTO sensorPatchDTO){
        log.debug("Partially updating sensor with id: {}", id);
        Sensor existingSensor = getSensorById(id);
        if (sensorPatchDTO.getName()!=null){
            existingSensor.setName(sensorPatchDTO.getName());
        }
        if (sensorPatchDTO.getRoomId()!=null){
            Room room = roomService.getRoomById(sensorPatchDTO.getRoomId());
            existingSensor.setRoom(room);
        }
        if (sensorPatchDTO.getValue()!=null){
            existingSensor.setValue(sensorPatchDTO.getValue());
        }
        Sensor updatedSensor = sensorRepository.save(existingSensor);
        eventPublisher.publishEvent(new SensorUpdatedEvent(updatedSensor));
        return updatedSensor;
    }
    @Transactional
    public void deleteSensor(Long id) {
        log.debug("Attempting to delete sensor with ID: {}", id);
        if (!sensorRepository.existsById(id)) {
            log.error("Cannot delete sensor - sensor not found with id: {}", id);
            throw new ResourceNotFoundException("Sensor not found with id: " + id);
        }
        sensorRepository.deleteById(id);
        log.info("Successfully deleted sensor with ID: {}", id);
    }

    public Page<Sensor> getSensorsByFilters(String name, Long roomId, SensorType type, Pageable pageable) {
        log.debug("Filtering sensors with pagination - Name: {}, Room ID: {}, Type: {}, Pageable: {}",
                name, roomId, type, pageable);
        String searchName = null;
        if (name != null && !name.isBlank()) {
            searchName = "%" + name.toLowerCase() + "%";
        }
        Page<Sensor> sensorPage = sensorRepository.findByFilters(searchName, roomId, type, pageable);
        log.info("Found {} sensors on page {} with filters - Name: {}, Room ID: {}, Type: {}",
                sensorPage.getNumberOfElements(), sensorPage.getNumber(), roomId, type);
        return sensorPage;
    }
}
