package com.smarthome.smart_home.dto.create;

import com.smarthome.smart_home.enums.SensorType;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "Модель для создания сенсора")
public class SensorCreateDTO {
    @Schema(description = "Название сенсора", example = "Датчик температуры")
    @NotBlank(message = "Sensor name is required")
    private String name;
    @Schema(description = "Тип сенсора", example = "TEMPERATURE")
    @NotNull(message = "Sensor type is required")
    private SensorType type;
    @Schema(description = "ID комнаты, в которой находится сенсор", example = "1")
    @NotNull(message = "Room ID is required")
    private Long roomId;
    @Schema(description = "Значение сенсора (если применимо)", example = "22.5")
    private Double value;
}
