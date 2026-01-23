package com.smarthome.smart_home.model;

import java.util.UUID;

import com.smarthome.smart_home.enums.SensorType;

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
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "sensors")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Sensor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique=true, nullable=false)
    private UUID uuid = UUID.randomUUID();

    @NotBlank(message = "Sensor name is required")
    @Column(nullable = false)
    private String name;

    @NotNull(message = "Sensor type is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SensorType type;

    @Column(nullable = false)
    private Double value;

    @NotNull(message = "Room is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    @com.fasterxml.jackson.annotation.JsonIgnore
    private Room room;

}