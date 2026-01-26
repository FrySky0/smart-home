package com.smarthome.smart_home.model;

import java.time.LocalTime;

import com.smarthome.smart_home.enums.automation.Action;
import com.smarthome.smart_home.enums.automation.TriggerEvent;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
    @Column(nullable = false)
    private String name;

    private String description;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trigger_device_id", nullable = false)
    private Device device;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trigger_sensor_id", nullable = true)
    private Sensor sensor;

    private boolean enabled = true;
    @Enumerated(EnumType.STRING)
    private TriggerEvent triggerEvent; 
    private Double triggerValue; 
    private LocalTime triggerTime;
    @Enumerated(EnumType.STRING)
    private Action action; 
    private Double actionValue;

}
