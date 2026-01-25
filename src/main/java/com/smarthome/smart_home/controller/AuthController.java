package com.smarthome.smart_home.controller;

import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.smarthome.smart_home.dto.auth.AuthRequestDTO;
import com.smarthome.smart_home.dto.auth.AuthResponseDTO;
import com.smarthome.smart_home.model.User;
import com.smarthome.smart_home.service.JwtService;
import com.smarthome.smart_home.service.TelegramService;
import com.smarthome.smart_home.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/auth")
@Slf4j
@Tag(name = "Authentication", description = "Аутентификация и регистрация пользователей")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final JwtService jwtService;
    private final TelegramService telegramService;

    @Value("${jwt.cookie-name}")
    private String cookieName;

    @Value("${jwt.expiration}")
    private Long expiration;

    @Operation(summary = "Регистрация нового пользователя", description = "Регистрация нового пользователя с указанием имени, email и пароля.")
    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> register(@Valid @RequestBody AuthRequestDTO authRequest, 
                                                   HttpServletResponse response) {
        log.info("Registering new user: {}", authRequest.getUsername());
        
        var user = userService.registerUser(
            authRequest.getUsername(), 
            authRequest.getEmail(),
            authRequest.getPassword()
        );

        String token = jwtService.generateToken(user.getUsername());
        setAuthCookie(response, token);

        log.info("User registered successfully: {}", user.getUsername());
        telegramService.sendLog("New user registered: " + user.getUsername());
        return ResponseEntity.ok(new AuthResponseDTO(user.getUsername(), user.getEmail(), "Registration successful"));
    }
    @Operation(summary = "Вход пользователя", description = "Аутентификация пользователя с указанием имени и пароля.")
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody AuthRequestDTO authRequest, 
                                                HttpServletResponse response) {
        log.info("Login attempt for user: {}", authRequest.getUsername());
        
        var user = userService.findByUsername(authRequest.getUsername());
        
        if (!userService.validatePassword(authRequest.getPassword(), user.getPassword())) {
            log.warn("Invalid password for user: {}", authRequest.getUsername());
            throw new RuntimeException("Invalid credentials");
        }

        String token = jwtService.generateToken(user.getUsername());
        setAuthCookie(response, token);

        log.info("User logged in successfully: {}", user.getUsername());
        telegramService.sendLog("User logged in: " + user.getUsername());
        return ResponseEntity.ok(new AuthResponseDTO(user.getUsername(), user.getEmail(), "Login successful"));
    }
    @Operation(summary = "Выход пользователя", description = "Выход пользователя и удаление аутентификационного cookie.")
    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(HttpServletResponse response) {
        Cookie cookie = new Cookie(cookieName, null);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);

        log.info("User logged out successfully");
        return ResponseEntity.ok(Map.of("message", "Logout successful"));
    }

    @Operation(summary = "Получить информацию о текущем пользователе", description = "Возвращает информацию о текущем аутентифицированном пользователе.")
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@AuthenticationPrincipal User user) {
        if (user == null) {
            log.warn("No authenticated user found");
            return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        }
        return ResponseEntity.ok(Map.of(
            "username", user.getUsername(),
            "email", user.getEmail(),
            "roles", user.getRoles().stream().map(Enum::name).collect(Collectors.toList())
        ));
    }

    private void setAuthCookie(HttpServletResponse response, String token) {
        Cookie cookie = new Cookie(cookieName, token);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge((int) (expiration / 1000));
        response.addCookie(cookie);
    }
}