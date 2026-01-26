package com.smarthome.smart_home.mappers;

import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import com.smarthome.smart_home.dto.response.UserResponseDTO;
import com.smarthome.smart_home.model.User;

@Component
public class UserMapper {

    public UserResponseDTO toResponseDTO(User user) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        
        // Маппим роли
        dto.setRoles(user.getRoles().stream()
                .map(Enum::name)
                .collect(Collectors.toSet()));

        // Извлекаем все права (Permissions)
        dto.setPermissions(user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(auth -> !auth.startsWith("ROLE_")) // Оставляем только права
                .collect(Collectors.toSet()));
        
        return dto;
    }
}