package com.example.festival_system.config;

import com.example.festival_system.model.Role;
import com.example.festival_system.model.User;
import com.example.festival_system.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AppInitialize {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostConstruct
    public void init() {
        createUserIfNotExists("superadmin@example.com", "password", Role.SUPER_ADMIN);
        createUserIfNotExists("admin@example.com", "password", Role.ADMIN);
        createUserIfNotExists("user@example.com", "password", Role.USER);
        createUserIfNotExists("organizer@example.com", "password", Role.ORGANIZER);
        createUserIfNotExists("alpha@example.com", "password", Role.ORGANIZER);
        createUserIfNotExists("beta@example.com", "password", Role.ORGANIZER);
        createUserIfNotExists("gamma@example.com", "password", Role.ORGANIZER);
    }

    private void createUserIfNotExists(String email, String rawPassword, Role role) {
        if (!userRepository.existsByEmail(email)) {
            User user = new User();
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode(rawPassword));
            user.setRole(role);
            userRepository.save(user);
            System.out.println("Создан пользователь " + role + ": " + email);
        } else {
            System.out.println("Пользователь уже существует: " + email);
        }
    }
}