package com.smarthome.smart_home.dto.update.patch;

import java.util.Set;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserPatchDTO {
    private String email;
    private Set<String> roles;
}