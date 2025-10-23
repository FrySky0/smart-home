package com.smarthome.smart_home.service;

import java.util.List;
import java.util.Random;

import org.springframework.context.ApplicationEventPublisher;
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

@Service
@RequiredArgsConstructor
public class SensorService {
    private final SensorRepository sensorRepository;
    private final RoomService roomService;
    // private final AutomationService automationService;
    private final ApplicationEventPublisher eventPublisher;

    public List<Sensor> getAllSensors() {
        return sensorRepository.findAll();
    }

    public Sensor getSensorById(Long id) {
        return sensorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sensor not found with id: " + id));
    }

    public Sensor createSensor(Sensor sensor, Long roomId) {
        Room room = roomService.getRoomById(roomId);
        sensor.setRoom(room);
        sensor.setValue(new Random().nextDouble() * 100); // рандомное число от 0 до 100
        eventPublisher.publishEvent(new SensorUpdatedEvent(sensor)); // публикуем событие обновления сенсора
        return sensorRepository.save(sensor);
    }

    public Sensor updateSensor(Long id, Sensor sensorDetails, Long roomId) {
        Sensor existingSensor = getSensorById(id);
        Room room = roomService.getRoomById(roomId);

        existingSensor.setName(sensorDetails.getName());
        existingSensor.setType(sensorDetails.getType());
        existingSensor.setRoom(room);

        return sensorRepository.save(existingSensor);
    }

    public void deleteSensor(Long id) {
        if (!sensorRepository.existsById(id)) {
            throw new ResourceNotFoundException("Sensor not found with id: " + id);
        }
        sensorRepository.deleteById(id);
    }

    public List<Sensor> getSensorsByRoomId(Long roomId) {
        return sensorRepository.findByRoomId(roomId);
    }

    public List<Sensor> getSensorsByType(SensorType type) {
        return sensorRepository.findByType(type);
    }

    public List<Sensor> getSensorsByFilters(Long roomId, SensorType type) {
        return sensorRepository.findByFilters(roomId, type);
    }

    public List<Sensor> getSensorsByFloor(Integer floor) {
        if (floor == null || floor < 0) {
            throw new ValidationException("Floor must be a positive number");
        }
        return sensorRepository.findByFloor(floor);
    }
}
