package com.RevHire.controller;

import com.RevHire.dto.ApplicationResponseDTO;
import com.RevHire.dto.EmployerApplicationDTO;
import com.RevHire.dto.NoteRequestDTO;
import com.RevHire.entity.Application;
import jakarta.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.RevHire.service.ApplicationService;

import java.util.List;

@Controller
@RequestMapping("/applications")
public class ApplicationController {

    private static final Logger logger = LogManager.getLogger(ApplicationController.class);

    @Autowired
    private ApplicationService applicationService;

    @GetMapping("/jobseeker/jobs/apply/{jobId}")
    public String showApplyPage(@PathVariable Long jobId, Model model, HttpSession session) {

        logger.info("Opening apply page for jobId: {}", jobId);

        Object userId = session.getAttribute("userId");
        if (userId == null) {
            logger.warn("User not logged in. Redirecting to login page.");
            return "redirect:/auth/login";
        }

        model.addAttribute("jobId", jobId);
        model.addAttribute("userId", userId);
        model.addAttribute("applications", List.of());

        logger.info("Apply page loaded successfully for jobId: {}", jobId);

        return "jobseeker/applications";
    }

    @PostMapping("/submit-application")
    public ResponseEntity<?> apply(@RequestParam Long jobId,
                                   @RequestParam Long userId,
                                   @RequestParam Long resumeId,
                                   @RequestParam(required = false) Long fileId, // ✅ NEW
                                   @RequestParam(required = false) String coverLetter) {

        logger.info("Submitting application for jobId: {} by userId: {}", jobId, userId);

        try {

            applicationService.applyJob(jobId, userId, resumeId, fileId, coverLetter); // ✅ UPDATED

            return ResponseEntity.ok("Application submitted successfully");

        } catch (RuntimeException e) {

            if (e.getMessage().contains("Already applied")) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
            }

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred");
        }
    }

    @GetMapping("/seeker/{seekerId}")
    public ResponseEntity<List<ApplicationResponseDTO>> getBySeeker(
            @PathVariable Long seekerId) {

        logger.info("Fetching applications for userId: {}", seekerId);

        return ResponseEntity.ok(
                applicationService.getApplicationsBySeeker(seekerId)
        );
    }

    @GetMapping("/job/{jobId}")
    public ResponseEntity<List<ApplicationResponseDTO>> getByJob(
            @PathVariable Long jobId) {

        logger.info("Fetching applications for jobId: {}", jobId);

        return ResponseEntity.ok(
                applicationService.getApplicationsByJob(jobId)
        );
    }
//    Employeruserid feteching apllications for employeruserid
    @GetMapping("/employer/{employerUserId}")
    @ResponseBody
    public ResponseEntity<List<EmployerApplicationDTO>> getByEmployer(@PathVariable Long employerUserId) {

        logger.info("Fetching applications for employerUserId: {}", employerUserId);

        return ResponseEntity.ok(
                applicationService.getApplicationsByEmployer(employerUserId)
        );
    }

    @PostMapping("/withdraw/{id}")
    public ResponseEntity<String> withdraw(@PathVariable Long id, @RequestParam String reason) {

        logger.warn("Application withdrawal requested. applicationId: {}", id);

        applicationService.withdrawApplication(id, reason);

        logger.info("Application withdrawn successfully. applicationId: {}", id);

        return ResponseEntity.ok("Application withdrawn successfully");
    }

    @PostMapping("/update-status/{id}")
    public ResponseEntity<String> updateStatus(@PathVariable Long id,
                                               @RequestParam String status) {

        logger.info("Updating application status. applicationId: {}, status: {}", id, status);

        applicationService.updateStatus(id, status);

        logger.info("Application status updated successfully. applicationId: {}", id);

        return ResponseEntity.ok("Application status updated successfully");
    }

    @PutMapping("/notes/{applicationId}")
    public ResponseEntity<?> addNotes(
            @PathVariable Long applicationId,
            @RequestBody NoteRequestDTO request) {

        logger.info("Adding employer notes to applicationId: {}", applicationId);

        return ResponseEntity.ok(
                applicationService.addEmployerNotes(
                        applicationId,
                        request.getEmployerId(),
                        request.getNoteText()
                )
        );
    }

    @GetMapping("/manage")
    public String showManageApplicationsPage(HttpSession session) {

        logger.info("Opening manage applications page");

        if (session.getAttribute("loggedInUser") == null) {
            logger.warn("Unauthorized access to manage applications page");
            return "redirect:/auth/login";
        }

        return "employer/applications/manage-applications";
    }

    @GetMapping("/all")
    @ResponseBody
    public ResponseEntity<List<EmployerApplicationDTO>> getAllApplications(HttpSession session) {

        logger.info("Fetching all applications for logged-in employer");

        Object userObj = session.getAttribute("loggedInUser");

        if (userObj == null) {

            logger.warn("Unauthorized request for employer applications");

            return ResponseEntity.status(401).build();
        }

        com.RevHire.entity.User user = (com.RevHire.entity.User) userObj;

        Long employerUserId = user.getUserId();

        List<EmployerApplicationDTO> applications =
                applicationService.getApplicationsByEmployer(employerUserId);

        logger.info("Total applications fetched: {}", applications.size());

        return ResponseEntity.ok(applications);
    }

    @GetMapping("/details/{applicationId}")
    @ResponseBody
    public ResponseEntity<EmployerApplicationDTO> getApplicationDetails(
            @PathVariable Long applicationId) {

        logger.info("Fetching application details for applicationId: {}", applicationId);

        List<EmployerApplicationDTO> apps =
                applicationService.getApplicationsByEmployer(null);

        EmployerApplicationDTO result = apps.stream()
                .filter(a -> a.getApplicationId().equals(applicationId))
                .findFirst()
                .orElseThrow(() -> {

                    logger.error("Application not found with ID: {}", applicationId);

                    return new RuntimeException("Application not found");
                });

        logger.info("Application details fetched successfully. applicationId: {}", applicationId);

        return ResponseEntity.ok(result);
    }
}