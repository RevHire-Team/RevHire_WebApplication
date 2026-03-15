package com.RevHire.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.RevHire.entity.User;
import com.RevHire.service.AuthService;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Controller
@RequestMapping("/auth")
public class AuthController {

    private static final Logger logger = LogManager.getLogger(AuthController.class);

    @Autowired
    private AuthService authService;

    // Show Register Page
    @GetMapping("/register")
    public String showRegisterPage(Model model) {

        logger.info("Opening registration page");

        model.addAttribute("user", new User());
        return "auth/register";
    }

    // Show Login Page
    @GetMapping("/login")
    public String showLoginPage() {

        logger.info("Opening login page");

        return "auth/login";
    }

    @GetMapping("/forgot-password")
    public String showResetPasswordPage() {

        logger.info("Opening forgot password page");

        return "auth/forgotpassword";
    }

    // Handle Register
    @PostMapping("/register")
    public String registerUser(@ModelAttribute User user, Model model) {

        logger.info("Register request received for email: {}", user.getEmail());

        try {

            authService.registerUser(user);

            logger.info("User registered successfully: {}", user.getEmail());

            return "redirect:/auth/login";

        } catch (RuntimeException e) {

            logger.error("User registration failed for email: {}", user.getEmail(), e);

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

        logger.info("Login attempt for email: {}", email);

        try {

            User user = authService.login(email, password);

            String role = String.valueOf(user.getRole());

            session.setAttribute("loggedInUser", user);
            session.setAttribute("userId", user.getUserId());
            session.setAttribute("role", user.getRole().name());

            logger.info("Login successful for userId: {} with role: {}", user.getUserId(), role);

            if (role != null && role.trim().equalsIgnoreCase("EMPLOYER")) {

                return "redirect:/employer/dashboard/" + user.getUserId();

            } else if (role != null && role.trim().equalsIgnoreCase("JOB_SEEKER")) {

                return "redirect:/jobseeker/dashboard/" + user.getUserId();

            } else if (role != null && role.trim().equalsIgnoreCase("ADMIN")) {

                return "redirect:/admin/dashboard";
            }

            return "redirect:/";

        } catch (Exception e) {

            logger.warn("Login failed for email: {}", email);

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

        logger.info("Password reset attempt for email: {}", email);

        try {

            authService.resetPassword(email, answer, newPassword);

            logger.info("Password reset successful for email: {}", email);

            model.addAttribute("success", "Password updated successfully");

            return "auth/login";

        } catch (Exception e) {

            logger.error("Password reset failed for email: {}", email, e);

            model.addAttribute("error", e.getMessage());

            return "auth/forgotpassword";
        }
    }

    // Show internal reset page
    @GetMapping("/reset-password")
    public String showInternalResetPage(HttpSession session) {

        logger.info("Opening internal password reset page");

        if (session.getAttribute("userId") == null) {

            logger.warn("Unauthorized access to reset password page");

            return "redirect:/auth/login";
        }

        return "auth/reset-password";
    }

    // Update password
    @PostMapping("/update-password")
    public String updatePassword(@RequestParam String currentPassword,
                                 @RequestParam String newPassword,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {

        Long userId = (Long) session.getAttribute("userId");

        if (userId == null) {

            logger.warn("Password update attempted without login");

            return "redirect:/auth/login";
        }

        try {

            authService.updatePassword(userId, currentPassword, newPassword);

            logger.info("Password updated successfully for userId: {}", userId);

            redirectAttributes.addFlashAttribute("success", "Password updated successfully!");

        } catch (RuntimeException e) {

            logger.error("Password update failed for userId: {}", userId, e);

            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/auth/reset-password";
    }

    @GetMapping("/delete")
    public String deleteAccount(HttpSession session, RedirectAttributes redirectAttributes) {

        Long userId = (Long) session.getAttribute("userId");

        if (userId != null) {

            logger.warn("Account deletion requested for userId: {}", userId);

            try {

                authService.deleteUser(userId);

                session.invalidate();

                logger.info("Account deleted successfully for userId: {}", userId);

                redirectAttributes.addFlashAttribute("success",
                        "Your account has been permanently deleted.");

                return "redirect:/";

            } catch (Exception e) {

                logger.error("Error deleting account for userId: {}", userId, e);

                redirectAttributes.addFlashAttribute("error",
                        "Error: " + e.getMessage());

                return "redirect:/auth/reset-password";
            }
        }

        logger.warn("Delete account attempted without login");

        return "redirect:/auth/login";
    }
}