package com.smarthome.smart_home.dto.importexport;

import java.util.UUID;

import lombok.Data;

@Data
public class SensorImportDTO {
    private String name;
    private UUID uuid;
    private String type;
}
