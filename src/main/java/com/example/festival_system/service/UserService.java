package com.example.festival_system.service;

import com.example.festival_system.model.User;
import java.util.List;

public interface UserService {
    boolean register(User user);
    boolean existsByEmail(String email);
    User findByEmail(String email);
    List<User> findAll();
    User findById(Long id);
    User save(User user);
    void deleteById(Long id);
}