package com.smarthome.smart_home.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.smarthome.smart_home.model.Sensor;

@Repository
public interface SensorRepository extends JpaRepository<Sensor, Long> {
    
}
