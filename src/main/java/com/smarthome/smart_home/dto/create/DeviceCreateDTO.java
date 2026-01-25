package com.smarthome.smart_home.dto.create;

import com.smarthome.smart_home.enums.DeviceStatus;
import com.smarthome.smart_home.enums.DeviceType;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "Модель для создания устройства")
public class DeviceCreateDTO {
    @Schema(description = "Название устройства", example = "Умная лампа")
    @NotBlank(message = "Device name is required")
    private String name;
    @Schema(description = "Тип устройства", example = "LIGHT")
    @NotNull(message = "Device type is required")
    private DeviceType type;
    @Schema(description = "Статус устройства", example = "ON")
    private DeviceStatus status;
    @Schema(description = "ID комнаты, в которой находится устройство", example = "1")
    @NotNull(message = "Room ID is required")
    private Long roomId;
    @Schema(description = "Значение устройства (если применимо)", example = "75.0")
    private Double value;
}
