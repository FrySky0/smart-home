package com.smarthome.smart_home.controller;


import java.util.List;
import java.util.stream.Collectors;

import org.postgresql.replication.LogSequenceNumber;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.smarthome.smart_home.dto.response.UserResponseDTO;
import com.smarthome.smart_home.dto.update.put.UserPutDTO;
import com.smarthome.smart_home.dto.update.patch.UserPatchDTO;
import com.smarthome.smart_home.enums.Role;
import com.smarthome.smart_home.enums.activitylog.ComponentName;
import com.smarthome.smart_home.enums.activitylog.LogAction;
import com.smarthome.smart_home.mappers.UserMapper;
import com.smarthome.smart_home.model.User;
import com.smarthome.smart_home.service.LogService;
import com.smarthome.smart_home.service.TelegramService;
import com.smarthome.smart_home.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/users")
@Slf4j
@Tag(name = "Users", description = "Управление пользователями")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;
    private final TelegramService telegramService;
    private final LogService logService;
    @GetMapping
    @Operation(summary = "Получить всех пользователей с возможностью ильтрации", description = "Требуется право 'users:manage'")
    @PreAuthorize("hasAuthority('users:manage')")
    public ResponseEntity<Page<UserResponseDTO>> getAllUsers(
        @RequestParam(required=false) String username,
        @RequestParam(required=false) String email,
        @RequestParam(required=false) Role role,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "id") String sortBy,
        @RequestParam(defaultValue = "asc") String sortDirection) {


        log.info(
                "Getting all users with filters - username: {}, email: {}, role: {}, page: {}, size: {}, sortBy: {}, sortDirection: {}",
                username, email, role, page, size, sortBy, sortDirection);
        Sort sort = sortDirection.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<UserResponseDTO> userPage = userService.getUsersByFilters(username,email,role,pageable)
                .map(userMapper::toResponseDTO);

        log.info("Successfully retrieved {} users on page {}", userPage.getNumberOfElements(), page);
        return ResponseEntity.ok(userPage);
    }

    @Operation(summary = "Получить пользователя по ID", description = "Получить сведения о пользователе по его уникальному идентификатору.")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('users:manage')")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id) {
        log.info("Getting user by ID: {}", id);

        User user = userService.getUserById(id);
        log.info("Device user with ID: {}", id);
        return ResponseEntity.ok(userMapper.toResponseDTO(user));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Полностью обновить пользователя")
    @PreAuthorize("hasAuthority('users:manage')")
    public ResponseEntity<UserResponseDTO> updateUserFull(@PathVariable Long id, @Valid @RequestBody UserPutDTO userPutDTO) {
        log.info("Fully updating user with ID: {}", id);
        User user = userService.updateFull(id, userPutDTO);
        UserResponseDTO userDTO = userMapper.toResponseDTO(user);
        log.info("The user with ID {} has been successfully fully updated", id);
        telegramService.sendLog("User " + user.getUsername() + " (ID: " + user.getId() + ") fully updated");
        logService.logEntity(ComponentName.User, LogAction.UPDATED, userDTO);
        return ResponseEntity.ok(userDTO);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Частично обновить пользователя")
    @PreAuthorize("hasAuthority('users:manage')")
    public ResponseEntity<UserResponseDTO> updateUserPartially(@PathVariable Long id, @Valid @RequestBody UserPatchDTO userPatchDTO) {
        log.info("Partially updating user with ID: {}", id);
        User user = userService.updatePartially(id, userPatchDTO);
        UserResponseDTO userDTO = userMapper.toResponseDTO(user);
        log.info("The user with ID {} has been successfully partially updated", id);
        telegramService.sendLog("User " + user.getUsername() + " (ID: " + user.getId() + ") partially updated");
        logService.logEntity(ComponentName.User, LogAction.UPDATED, userDTO);
        return ResponseEntity.ok(userDTO);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить пользователя")
    @PreAuthorize("hasAuthority('users:manage')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        log.info("Deleting user with ID: {}", id);

        userService.deleteUser(id);

        log.info("Successfully deleted user with ID: {}", id);
        telegramService.sendLog("User with ID " + id + " has been deleted");
        logService.log(ComponentName.User, LogAction.DELETED, "ID:"+id);
        return ResponseEntity.noContent().build();
    }
}
