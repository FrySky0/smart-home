package com.smarthome.smart_home.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@RequiredArgsConstructor
public enum Role {
    ROLE_USER(Set.of(
        Permission.ROOM_READ,
        Permission.ROOM_CONTROL,
        Permission.ROOM_MANAGE,

        Permission.DEVICE_READ,
        Permission.DEVICE_CONTROL,

        Permission.SENSOR_READ,
        Permission.SENSOR_MANAGE,

        Permission.AUTOMATIONRULE_READ,
        Permission.AUTOMATIONRULE_CONTROL,
        Permission.AUTOMATIONRULE_MANAGE
    )),
    ROLE_ADMIN(Set.of(
        Permission.ROOM_READ,
        Permission.ROOM_CONTROL,
        Permission.ROOM_MANAGE,

        Permission.DEVICE_READ,
        Permission.DEVICE_CONTROL,
        Permission.DEVICE_MANAGE,

        Permission.SENSOR_READ,
        Permission.SENSOR_CONTROL,
        Permission.SENSOR_MANAGE,

        Permission.AUTOMATIONRULE_READ,
        Permission.AUTOMATIONRULE_CONTROL,
        Permission.AUTOMATIONRULE_MANAGE,

        Permission.SYSTEMREPORT_DOWNLOAD,

        Permission.CONFIGURATION_EXPORT,
        Permission.CONFIGURATION_IMPORT,

        Permission.USERS_MANAGE
    ));

    @Getter
    private final Set<Permission> permissions;

    public Set<SimpleGrantedAuthority> getAuthorities() {
        var authorities = getPermissions()
                .stream()
                .map(permission -> new SimpleGrantedAuthority(permission.getPermission()))
                .collect(Collectors.toSet());
        authorities.add(new SimpleGrantedAuthority(this.name()));
        return authorities;
    }
}