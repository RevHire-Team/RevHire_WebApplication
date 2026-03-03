package com.RevHire.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.RevHire.entity.User;
import com.RevHire.service.AuthService;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    // Show Register Page
    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        model.addAttribute("user", new User());
        return "auth/register";
    }

    // Show Login Page
    @GetMapping("/login")
    public String showLoginPage() {
        return "auth/login";
    }

    @GetMapping("/forgot-password")
    public String showResetPasswordPage() {
        return "auth/forgotpassword";
    }

    // Handle Register
    @PostMapping("/register")
    public String registerUser(@ModelAttribute User user, Model model) {
        try {
            authService.registerUser(user);
            return "redirect:/auth/login";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("user", user);
            return "register";
        }
    }

    // Handle login form submission
    @PostMapping("/login")
    public String login(@RequestParam String email,
                        @RequestParam String password,
                        HttpSession session,
                        Model model) {

        try {

            User user = authService.login(email, password);

            String role = String.valueOf(user.getRole());

            session.setAttribute("loggedInUser", user);
            session.setAttribute("userId", user.getUserId());

            System.out.println("ROLE FROM DB: [" + role + "]"); //

            if (role != null && role.trim().equalsIgnoreCase("EMPLOYER")) {

                return "redirect:/employer/dashboard/" + user.getUserId();

            } else if (role != null && role.trim().equalsIgnoreCase("JOB_SEEKER")) {

                return "redirect:/jobseeker/dashboard/" + user.getUserId();

            } else if (role != null && role.trim().equalsIgnoreCase("ADMIN")) {

                return "redirect:/admin/dashboard";
            }

            return "redirect:/";

        } catch (Exception e) {

            model.addAttribute("error", "Invalid email or password");
            model.addAttribute("email", email);

            return "auth/login";
        }
    }

    @PostMapping("/forgot-password")
    public String resetPassword(@RequestParam String email,
                                @RequestParam String answer,
                                @RequestParam String newPassword,
                                Model model) {

        try {
            authService.resetPassword(email, answer, newPassword);
            model.addAttribute("success", "Password updated successfully");
            return "auth/login";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "auth/forgotpassword";
        }
    }

    // 1. Add this to show the page
    @GetMapping("/reset-password")
    public String showInternalResetPage(HttpSession session) {
        if (session.getAttribute("userId") == null) {
            return "redirect:/auth/login";
        }
        return "auth/reset-password"; // Points to templates/auth/reset-password.html
    }

    // 2. Update the POST method redirect
    @PostMapping("/update-password")
    public String updatePassword(@RequestParam String currentPassword,
                                 @RequestParam String newPassword,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {

        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/auth/login";
        }

        try {
            authService.updatePassword(userId, currentPassword, newPassword);
            redirectAttributes.addFlashAttribute("success", "Password updated successfully!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        // FIXED: Redirects back to the GET mapping above
        return "redirect:/auth/reset-password";
    }

    @GetMapping("/delete")
    public String deleteAccount(HttpSession session, RedirectAttributes redirectAttributes) {
        Long userId = (Long) session.getAttribute("userId");

        if (userId != null) {
            try {
                // ACTIVATE THIS LINE: Call the service to remove from DB
                authService.deleteUser(userId);

                // Clear the session so the user is logged out
                session.invalidate();

                redirectAttributes.addFlashAttribute("success", "Your account has been permanently deleted.");
                return "redirect:/";

            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
                return "redirect:/auth/reset-password";
            }
        }
        return "redirect:/auth/login";
    }

}