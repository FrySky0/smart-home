package com.smarthome.smart_home.service;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.smarthome.smart_home.dto.auth.PasswordChangeRequestDTO;
import com.smarthome.smart_home.dto.update.patch.UserPatchDTO;
import com.smarthome.smart_home.dto.update.put.UserPutDTO;
import com.smarthome.smart_home.enums.Role;
import com.smarthome.smart_home.model.User;
import com.smarthome.smart_home.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    public User registerUser(String username, String email, String password) {
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("Username already exists");
        }
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRoles(Collections.singleton(Role.ROLE_USER));

        User savedUser = userRepository.save(user);
        log.info("User registered successfully: {}", username);
        return savedUser;
    }
    @Transactional
    public void changePassword(User user, PasswordChangeRequestDTO request) {
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new RuntimeException("Неверный текущий пароль");
        }

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("Новые пароли не совпадают");
        }
        
        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            throw new RuntimeException("Новый пароль не должен совпадать со старым");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        log.info("Пароль пользователя {} успешно изменен", user.getUsername());
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public boolean validatePassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Transactional
    public User updateFull(Long id, UserPutDTO dto) {
        User user = getUserById(id);
        user.setEmail(dto.getEmail());
        Set<Role> roles = dto.getRoles().stream()
                .map(Role::valueOf)
                .collect(Collectors.toSet());
        user.setRoles(roles);
        return userRepository.save(user);
    }

    @Transactional
    public User updatePartially(Long id, UserPatchDTO dto) {
        User user = getUserById(id);
        if (dto.getEmail() != null) user.setEmail(dto.getEmail());
        if (dto.getRoles() != null){
            Set<Role> roles = dto.getRoles().stream()
                    .map(Role::valueOf)
                    .collect(Collectors.toSet());
            user.setRoles(roles);
        }
        return userRepository.save(user);
    }


    @Transactional
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public Page<User> getUsersByFilters(String username, String email, Role role, Pageable pageable) {
        log.debug("Fetching users with filters - username: {}, email: {}, pageable: {}",
                username, email, pageable);
        String searchUsername = null;
        if (username != null && !username.isBlank()) {
            searchUsername = "%" + username.toLowerCase() + "%";
        }
        String searchEmail = null;
        if (email != null && !email.isBlank()) {
            searchEmail = "%" + email.toLowerCase() + "%";
        }
        Page<User> users = userRepository.findByFilters(searchUsername,searchEmail, role, pageable);
        log.info("Successfully fetched {} users with applied filters", users.getTotalElements());
        return users;
    }
}