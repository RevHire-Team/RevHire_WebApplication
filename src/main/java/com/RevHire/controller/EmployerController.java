package com.RevHire.controller;

import com.RevHire.dto.EmployerProfileDTO;
import com.RevHire.entity.User;
import com.RevHire.service.EmployerService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/employer")
@RequiredArgsConstructor
public class EmployerController {

    private final EmployerService employerService;

    @PostMapping("/profile/{userId}")
    public ResponseEntity<?> createOrUpdateProfile(
            @PathVariable Long userId,
            @RequestBody EmployerProfileDTO dto) {

        // Calls the modified service above
        EmployerProfileDTO updatedDto = employerService.createOrUpdateProfile(userId, dto);

        return ResponseEntity.ok(updatedDto);
    }

    @GetMapping("/profile/edit")
    public String showEditProfilePage(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) return "redirect:/auth/login";

        EmployerProfileDTO profile = employerService.getProfile(user.getUserId());
        // If profile is null, send an empty DTO to avoid template errors
        model.addAttribute("profile", profile != null ? profile : new EmployerProfileDTO());
        model.addAttribute("userId", user.getUserId()); // Needed for the API call

        return "employer/edit-company-profile";
    }

    @GetMapping("/profile")
    public String viewProfilePage(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedInUser");

        if (user == null) {
            return "redirect:/auth/login";
        }

        EmployerProfileDTO profile = employerService.getProfile(user.getUserId());

        model.addAttribute("profile", profile);

        return "employer/companyprofile";
    }

    @GetMapping("/dashboard/{employerId}")
    public String showDashboard(@PathVariable Long employerId, Model model) {

        model.addAttribute("dashboard",
                employerService.getDashboard(employerId));

        return "employer/EmployeerDashboard"; // Thymeleaf file name
    }

//    @GetMapping("/dashboard")
//    public String redirectToDashboard(HttpSession session) {
//        User user = (User) session.getAttribute("loggedInUser");
//        if (user == null) {
//            return "redirect:/auth/login";
//        }
//        // This takes the user to /employer/dashboard/5 (or whatever their ID is)
//        return "redirect:/employer/dashboard/" + user.getUserId();
//    }

}