package com.example.festival_system.controller;

import com.example.festival_system.model.Role;
import com.example.festival_system.model.User;
import com.example.festival_system.repository.UserRepository;
import com.example.festival_system.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/users")
@PreAuthorize("hasRole('SUPER_ADMIN')")
public class UserWebController {

    @Autowired
    private JwtUtil jwtUtil;

    private final UserRepository userRepository;

    public UserWebController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping
    public String userPage(Model model, @RequestParam(required = false) String msg) {
        model.addAttribute("users", userRepository.findAll());
        model.addAttribute("message", msg);
        return "users";
    }

    @PostMapping("/update")
    public String updateRole(@RequestParam Long id,
                             @RequestParam Role role,
                             RedirectAttributes redirectAttributes) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setRole(role);
            userRepository.save(user);
            redirectAttributes.addAttribute("msg", "Роль успешно обновлена.");
        } else {
            redirectAttributes.addAttribute("msg", "Пользователь не найден.");
        }
        return "redirect:/users";
    }

    @DeleteMapping("/delete/{id}")
    @ResponseBody
    public String deleteUser(@PathVariable Long id,
                             RedirectAttributes redirectAttributes) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return "Пользователь удалён.";
        } else {
            return "Пользователь не найден.";
        }
    }
}