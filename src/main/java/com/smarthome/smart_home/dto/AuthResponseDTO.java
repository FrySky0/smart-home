package com.smarthome.smart_home.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponseDTO {
    private String username;
    private String email;
    private String message;
}