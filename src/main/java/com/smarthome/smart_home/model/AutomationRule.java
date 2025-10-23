package com.smarthome.smart_home.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "automation_rules")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AutomationRule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable=false)
    private String name;

    private String description;

    // private Device triggerDevice;
    // private Sensor triggerSensor;

    private Long triggerDeviceId; // какой девайс будет изменен при триггере правила
    private Long triggerSensorId; // сенсор, который будет триггерить правило

    private boolean enabled = true;

    private String triggerEvent; // greater или less
    private Integer triggerValue; // значение сенсора по которому триггерится правило
    private String action; // как девайс будет изменен, turnOn или turnOff
}
