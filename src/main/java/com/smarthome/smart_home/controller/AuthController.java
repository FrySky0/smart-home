package com.smarthome.smart_home.controller;

import com.smarthome.smart_home.dto.AuthRequestDTO;
import com.smarthome.smart_home.dto.AuthResponseDTO;
import com.smarthome.smart_home.service.JwtService;
import com.smarthome.smart_home.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@Slf4j
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final JwtService jwtService;

    @Value("${jwt.cookie-name}")
    private String cookieName;

    @Value("${jwt.expiration}")
    private Long expiration;

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
        return ResponseEntity.ok(new AuthResponseDTO(user.getUsername(), user.getEmail(), "Registration successful"));
    }

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
        return ResponseEntity.ok(new AuthResponseDTO(user.getUsername(), user.getEmail(), "Login successful"));
    }

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

    @GetMapping("/me")
    public ResponseEntity<Map<String, String>> getCurrentUser() {
        return ResponseEntity.ok(Map.of("message", "You are authenticated"));
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