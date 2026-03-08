package com.RevHire.controller;

import com.RevHire.dto.ApplicationResponseDTO;
import com.RevHire.dto.EmployerApplicationDTO;
import com.RevHire.dto.NoteRequestDTO;
import com.RevHire.entity.Application;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.RevHire.service.ApplicationService;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/applications")
public class ApplicationController {

    @Autowired
    private ApplicationService applicationService;

    @GetMapping("/jobseeker/jobs/apply/{jobId}")
    public String showApplyPage(@PathVariable Long jobId, Model model, HttpSession session) {
        // 1. Check if user is logged in
        Object userId = session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/auth/login";
        }

        // 2. Pass the jobId to the frontend
        model.addAttribute("jobId", jobId);
        model.addAttribute("userId", userId);

        // 3. Return the name of your application form HTML file (apply-form.html)
        return "jobseeker/applications";
    }

    @PostMapping("/submit-application")
    public ResponseEntity<?> apply(@RequestParam Long jobId,
                                   @RequestParam Long seekerId,
                                   @RequestParam Long resumeId,
                                   @RequestParam(required = false) String coverLetter) {
        try {
            applicationService.applyJob(jobId, seekerId, resumeId, coverLetter);
            return ResponseEntity.ok("Application submitted successfully");
        } catch (RuntimeException e) {
            if (e.getMessage().contains("Already applied")) {
                // Return 409 Conflict so the frontend knows it's a duplicate
                return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred");
        }
    }

    @GetMapping("/seeker/{seekerId}")
    public ResponseEntity<List<ApplicationResponseDTO>> getBySeeker(
            @PathVariable Long seekerId) {

        return ResponseEntity.ok(
                applicationService.getApplicationsBySeeker(seekerId)
        );
    }

    // Employer views applications of job
    @GetMapping("/job/{jobId}")
    public ResponseEntity<List<ApplicationResponseDTO>> getByJob(
            @PathVariable Long jobId) {

        return ResponseEntity.ok(
                applicationService.getApplicationsByJob(jobId)
        );
    }

    // Employer views all applications
    @GetMapping("/employer/{employerUserId}")
    @ResponseBody
    public ResponseEntity<List<EmployerApplicationDTO>> getByEmployer(@PathVariable Long employerUserId) {
        return ResponseEntity.ok(applicationService.getApplicationsByEmployer(employerUserId));
    }

    @PostMapping("/withdraw/{id}")
    public ResponseEntity<String> withdraw(@PathVariable Long id, @RequestParam String reason) {

        applicationService.withdrawApplication(id, reason);
        return ResponseEntity.ok("Application withdrawn successfully");
    }

    @PostMapping("/update-status/{id}")
    public ResponseEntity<String> updateStatus(@PathVariable Long id,
                                               @RequestParam String status) {

        applicationService.updateStatus(id, status);
        return ResponseEntity.ok("Application status updated successfully");
    }

    // Add notes
        @PutMapping("/notes/{applicationId}")
    public ResponseEntity<?> addNotes(
            @PathVariable Long applicationId,
            @RequestBody NoteRequestDTO request) {

        return ResponseEntity.ok(
                applicationService.addEmployerNotes(
                        applicationId,
                        request.getEmployerId(),
                        request.getNoteText()
                )
        );
    }

    // Add to ApplicationController.java

    @GetMapping("/manage")
    public String showManageApplicationsPage(HttpSession session) {
        if (session.getAttribute("loggedInUser") == null) return "redirect:/auth/login";
        return "employer/applications/manage-applications"; // Path to your HTML file
    }

    // API to fetch all applications for the logged-in employer
    @GetMapping("/all")
    @ResponseBody
    public ResponseEntity<List<EmployerApplicationDTO>> getAllApplications(HttpSession session) {
        Object userObj = session.getAttribute("loggedInUser");
        if (userObj == null) return ResponseEntity.status(401).build();

        com.RevHire.entity.User user = (com.RevHire.entity.User) userObj;  // cast to User
        Long employerUserId = user.getUserId();                             // extract ID

        List<EmployerApplicationDTO> applications = applicationService.getApplicationsByEmployer(employerUserId);
        return ResponseEntity.ok(applications);
    }
    @GetMapping("/details/{applicationId}")
    @ResponseBody
    public ResponseEntity<EmployerApplicationDTO> getApplicationDetails(
            @PathVariable Long applicationId) {

        List<EmployerApplicationDTO> apps =
                applicationService.getApplicationsByEmployer(null);

        EmployerApplicationDTO result = apps.stream()
                .filter(a -> a.getApplicationId().equals(applicationId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Application not found"));

        return ResponseEntity.ok(result);
    }


}

