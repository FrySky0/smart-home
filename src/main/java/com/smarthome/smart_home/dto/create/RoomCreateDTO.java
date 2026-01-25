package com.smarthome.smart_home.dto.create;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "Модель для создания комнаты")
public class RoomCreateDTO {
    @Schema(description = "Название комнаты", example = "Гостиная")
    @NotBlank(message = "Room name is required")
    private String name;
    @Schema(description = "Этаж, на котором находится комната", example = "2")
    @NotNull(message = "Floor is required")
    private Integer floor;
}