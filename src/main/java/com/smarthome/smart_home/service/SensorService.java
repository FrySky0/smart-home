package com.smarthome.smart_home.service;

import java.util.List;
import java.util.UUID;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.smarthome.smart_home.dto.CreateSensorDTO;
import com.smarthome.smart_home.dto.SensorUpdateDTO;
import com.smarthome.smart_home.enums.SensorType;
import com.smarthome.smart_home.events.SensorUpdatedEvent;
import com.smarthome.smart_home.exception.ResourceNotFoundException;
import com.smarthome.smart_home.exception.ValidationException;
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
    // private final AutomationService automationService;
    private final ApplicationEventPublisher eventPublisher;

    public List<Sensor> getAllSensors() {
        log.debug("Retrieving all sensors");
        List<Sensor> sensors = sensorRepository.findAll();
        log.info("Successfully retrieved {} sensors", sensors.size());
        return sensors;
    }

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

    public Sensor createSensor(CreateSensorDTO createSensorDTO) {
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

    public Sensor updateFull(Long id, SensorUpdateDTO sensorUpdateDTO){
        log.debug("Fully updating sensor with id: {}", id);
        Sensor existingSensor = getSensorById(id);
        Room room = roomService.getRoomById(sensorUpdateDTO.getRoomId());
        existingSensor.setName(sensorUpdateDTO.getName());
        existingSensor.setValue(sensorUpdateDTO.getValue());
        existingSensor.setRoom(room);
        Sensor updatedSensor = sensorRepository.save(existingSensor);
        log.debug("Successfully fully updated sensor with id: {}", id);
        return updatedSensor;
    }

    public Sensor updatePartially(Long id, SensorUpdateDTO sensorUpdateDTO){
        log.debug("Partially updating sensor with id: {}", id);
        Sensor existingSensor = getSensorById(id);
        if (sensorUpdateDTO.getName()!=null){
            existingSensor.setName(sensorUpdateDTO.getName());
        }
        if (sensorUpdateDTO.getRoomId()!=null){
            Room room = roomService.getRoomById(sensorUpdateDTO.getRoomId());
            existingSensor.setRoom(room);
        }
        if (sensorUpdateDTO.getValue()!=null){
            existingSensor.setValue(sensorUpdateDTO.getValue());
        }
        Sensor updatedSensor = sensorRepository.save(existingSensor);
        return updatedSensor;
    }

    public void deleteSensor(Long id) {
        log.debug("Attempting to delete sensor with ID: {}", id);
        if (!sensorRepository.existsById(id)) {
            log.error("Cannot delete sensor - sensor not found with id: {}", id);
            throw new ResourceNotFoundException("Sensor not found with id: " + id);
        }
        sensorRepository.deleteById(id);
        log.info("Successfully deleted sensor with ID: {}", id);
    }

    public List<Sensor> getSensorsByRoomId(Long roomId) {
        log.debug("Retrieving sensors for room ID: {}", roomId);
        List<Sensor> sensors = sensorRepository.findByRoomId(roomId);
        log.info("Found {} sensors for room ID: {}", sensors.size(), roomId);
        return sensors;
    }

    public List<Sensor> getSensorsByType(SensorType type) {
        log.debug("Retrieving sensors of type: {}", type);
        List<Sensor> sensors = sensorRepository.findByType(type);
        log.info("Found {} sensors of type: {}", sensors.size(), type);
        return sensors;
    }

    public List<Sensor> getSensorsByFilters(Long roomId, SensorType type) {
        log.debug("Filtering sensors - Room ID: {}, Type: {}", roomId, type);
        List<Sensor> sensors = sensorRepository.findByFilters(roomId, type);
        log.info("Found {} sensors matching filters - Room ID: {}, Type: {}",
                sensors.size(), roomId, type);
        return sensors;
    }

    public Page<Sensor> getSensorsByFilters(Long roomId, SensorType type, Pageable pageable) {
        log.debug("Filtering sensors with pagination - Room ID: {}, Type: {}, Pageable: {}",
                roomId, type, pageable);
        Page<Sensor> sensorPage = sensorRepository.findByFilters(roomId, type, pageable);
        log.info("Found {} sensors on page {} with filters - Room ID: {}, Type: {}",
                sensorPage.getNumberOfElements(), sensorPage.getNumber(), roomId, type);
        return sensorPage;
    }

    public List<Sensor> getSensorsByFloor(Integer floor) {
        log.debug("Retrieving sensors for floor: {}", floor);
        if (floor == null || floor < 0) {
            log.error("Invalid floor value: {}", floor);
            throw new ValidationException("Floor must be a positive number");
        }
        List<Sensor> sensors = sensorRepository.findByFloor(floor);
        log.info("Found {} sensors on floor {}", sensors.size(), floor);
        return sensors;
    }
}
