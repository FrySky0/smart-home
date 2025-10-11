package com.smarthome.smart_home.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateRoomDTO {
    @NotBlank(message = "Room name is required")
    private String name;

    @NotNull(message = "Floor is required")
    private Integer floor;
}