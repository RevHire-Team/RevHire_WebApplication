package com.RevHire.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.RevHire.entity.User;
import com.RevHire.service.AuthService;

@Controller
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute User user, Model model) {
        authService.registerUser(user);
        model.addAttribute("success", "Registration successful!");
        return "login";
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String email,
                        @RequestParam String password,
                        Model model) {

        try {
            User user = authService.login(email, password);
            return "redirect:/dashboard";
        } catch (Exception e) {
            model.addAttribute("error", "Invalid credentials");
            return "login";
        }
    }

    @PostMapping("/reset-password")
    public String resetPassword(@RequestParam String email,
                                @RequestParam String answer,
                                @RequestParam String newPassword,
                                Model model) {

        try {
            authService.resetPassword(email, answer, newPassword);
            model.addAttribute("success", "Password updated");
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }

        return "login";
    }
}