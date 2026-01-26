package com.smarthome.smart_home.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PasswordChangeRequestDTO {
    @NotBlank(message = "Текущий пароль обязателен")
    private String currentPassword;

    @NotBlank(message = "Новый пароль обязателен")
    private String newPassword;

    @NotBlank(message = "Подтверждение пароля обязательно")
    private String confirmPassword;
}