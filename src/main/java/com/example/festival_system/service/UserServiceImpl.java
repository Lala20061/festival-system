package com.example.festival_system.service;

import com.example.festival_system.model.User;
import com.example.festival_system.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository repo, PasswordEncoder encoder) {
    }

    @Override
    public boolean register(User user) {
        if (user == null || user.getEmail() == null) return false;
        if (userRepository.existsByEmail(user.getEmail())) return false;
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return true;
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public User findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    @Override
    public User save(User user) {
        if (user.getId() != null) {
            User current = userRepository.findById(user.getId()).orElse(null);
            if (current != null) {
                String incoming = user.getPassword();
                if (incoming == null || incoming.isBlank() || incoming.equals(current.getPassword())) {
                    user.setPassword(current.getPassword());
                } else {
                    user.setPassword(passwordEncoder.encode(incoming));
                }
            }
        } else {
            if (user.getPassword() != null && !user.getPassword().isBlank()) {
                user.setPassword(passwordEncoder.encode(user.getPassword()));
            }
        }
        return userRepository.save(user);
    }

    @Override
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }
}