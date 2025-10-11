package com.smarthome.smart_home.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.smarthome.smart_home.enums.SensorType;
import com.smarthome.smart_home.model.Sensor;
import com.smarthome.smart_home.repository.SensorRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SensorService {
    private final SensorRepository sensorRepository;

    public List<Sensor> getAllSensors() {
        return sensorRepository.findAll();
    }
    public Sensor getSensorById(Long id){
        return sensorRepository.findById(id).orElse(null);
    }

    public List<Sensor> getSensorsByRoomId(Long roomId){
        return sensorRepository.findByRoomId(roomId);
    }

    public List<Sensor> getSensorsByType(SensorType type){
        return sensorRepository.findByType(type);
    }

    public List<Sensor> getSensorsByFilters(Long roomId, SensorType type){
        return sensorRepository.findByFilters(roomId, type);
    }

    public List<Sensor> getSensorsByFloor(Integer floor) {
        return sensorRepository.findByFloor(floor);
    }
}
