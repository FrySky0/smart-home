package com.smarthome.smart_home.service;

import java.util.List;
import java.util.Random;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.smarthome.smart_home.enums.SensorType;
import com.smarthome.smart_home.events.SensorUpdatedEvent;
import com.smarthome.smart_home.exception.ResourceNotFoundException;
import com.smarthome.smart_home.exception.ValidationException;
import com.smarthome.smart_home.model.Room;
import com.smarthome.smart_home.model.Sensor;
import com.smarthome.smart_home.repository.SensorRepository;
import com.smarthome.smart_home.service.automation.AutomationService;

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

    public Sensor createSensor(Sensor sensor, Long roomId) {
        log.debug("Creating new sensor for room ID: {}", roomId);
        Room room = roomService.getRoomById(roomId);
        sensor.setRoom(room);
        sensor.setValue(new Random().nextDouble() * 100); // random number from 0 to 100

        log.debug("Setting initial sensor value: {}", sensor.getValue());
        eventPublisher.publishEvent(new SensorUpdatedEvent(sensor)); // publish sensor update event

        Sensor savedSensor = sensorRepository.save(sensor);
        log.info("Successfully created sensor with ID: {} - {} in room: {}",
                savedSensor.getId(), savedSensor.getName(), room.getName());
        return savedSensor;
    }

    public Sensor updateSensor(Long id, Sensor sensorDetails, Long roomId) {
        log.debug("Updating sensor with ID: {}", id);
        Sensor existingSensor = getSensorById(id);
        Room room = roomService.getRoomById(roomId);

        log.debug("Updating sensor details - Name: {}, Type: {}, Room ID: {}",
                sensorDetails.getName(), sensorDetails.getType(), roomId);

        existingSensor.setName(sensorDetails.getName());
        existingSensor.setType(sensorDetails.getType());
        existingSensor.setRoom(room);

        Sensor updatedSensor = sensorRepository.save(existingSensor);
        log.info("Successfully updated sensor with ID: {}", id);
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
