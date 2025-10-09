package com.smarthome.smart_home.model;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;

import lombok.NoArgsConstructor;

@Entity
@Table(name = "devices")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Device {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeviceType type;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeviceStatus status;
    
    @Column(nullable = false)
    private String room;
    
    public enum DeviceType {
        LIGHT, THERMOSTAT, AIR_CONDITIONER, TV
    }
    
    public enum DeviceStatus {
        ON, OFF
    }
}