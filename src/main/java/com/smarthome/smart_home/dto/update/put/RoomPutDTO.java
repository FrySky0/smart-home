package com.smarthome.smart_home.dto.update.put;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RoomPutDTO {
    @NotBlank(message = "Room name is required")
    private String name;
    @NotNull(message = "Floor is required")
    private Integer floor;
}
