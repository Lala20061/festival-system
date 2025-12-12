package com.example.festival_system.controller;

import com.example.festival_system.model.Role;
import com.example.festival_system.model.User;
import com.example.festival_system.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@PreAuthorize("hasRole('SUPER_ADMIN')")
public class UserRestController {

    @Autowired
    private UserService userService;

    @GetMapping
    public List<User> getAllUsers() {
        return userService.findAll();
    }

    @PutMapping("/{id}/role")
    public ResponseEntity<?> changeUserRole(@PathVariable Long id, @RequestParam Role role) {
        User user = userService.findById(id);
        if (user == null) {
            return ResponseEntity.status(404).body("Пользователь не найден");
        }
        user.setRole(role);
        userService.save(user);
        return ResponseEntity.ok("Роль пользователя обновлена");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        User user = userService.findById(id);
        if (user == null) {
            return ResponseEntity.status(404).body("Пользователь не найден");
        }
        userService.deleteById(id);
        return ResponseEntity.ok("Пользователь удалён");
    }
}