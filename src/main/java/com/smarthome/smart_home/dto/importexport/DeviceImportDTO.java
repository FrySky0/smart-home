package com.smarthome.smart_home.dto.importexport;

import java.util.UUID;

import lombok.Data;

@Data
public class DeviceImportDTO {
    private String name;
    private UUID uuid;
    private String type;
}
