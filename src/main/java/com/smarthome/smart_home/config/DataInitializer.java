package com.smarthome.smart_home.config;

import com.smarthome.smart_home.enums.Role;
import com.smarthome.smart_home.model.User;
import com.smarthome.smart_home.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.findByUsername("admin").isEmpty()) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setEmail("admin@smarthome.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRoles(Set.of(Role.ROLE_ADMIN, Role.ROLE_USER));
            
            userRepository.save(admin);
            log.info("Default admin user created: admin / admin123");
        }

        if (userRepository.findByUsername("user").isEmpty()) {
            User user = new User();
            user.setUsername("user");
            user.setEmail("user@smarthome.com");
            user.setPassword(passwordEncoder.encode("user123"));
            user.setRoles(Set.of(Role.ROLE_USER));
            
            userRepository.save(user);
            log.info("Default user created: user / user123");
        }
    }
}