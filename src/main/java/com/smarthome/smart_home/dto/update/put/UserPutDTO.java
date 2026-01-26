package com.smarthome.smart_home.dto.update.put;

import java.util.Set;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserPutDTO {
    @NotBlank(message = "User email is required")
    private String email;
    @NotNull(message = "User roles is required")
    private Set<String> roles;
}