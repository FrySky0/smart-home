package com.smarthome.smart_home.service;

import java.util.List;

import org.springframework.stereotype.Service;

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
}
