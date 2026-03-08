package com.RevHire.controller;

import com.RevHire.dto.EmployerDashboardDTO;
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

        // This calls your Service method which fetches from DB
        EmployerProfileDTO profile = employerService.getProfile(user.getUserId());

        // Pass the profile object to the template
        model.addAttribute("profile", profile);
        return "employer/company-profile"; // Refers to templates/employer/companyprofile.html
    }

    @GetMapping("/dashboard/{employerId}")
    public String showDashboard(@PathVariable Long employerId, Model model) {
        // Fetch the DTO
        EmployerDashboardDTO dashboard = employerService.getDashboard(employerId);

        // Pass to the view
        model.addAttribute("dashboard", dashboard);

        return "employer/dashboard";
    }

      @GetMapping("/employer/dashboard/{employerId}")
      public String getDashboard(@PathVariable Long employerId, Model model) {
          // Call the service
          EmployerDashboardDTO dashboard = employerService.getDashboard(employerId);

          // DEBUG: print to console
          System.out.println("Dashboard: " + dashboard);

          // Pass dashboard object to Thymeleaf
          model.addAttribute("dashboard", dashboard);

          // Return Thymeleaf template name (without .html)
          return "employer/dashboard";
      }

}
