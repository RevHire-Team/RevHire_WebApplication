package com.RevHire.controller;

import com.RevHire.dto.ApplicationResponseDTO;
import com.RevHire.dto.EmployerApplicationDTO;
import com.RevHire.dto.NoteRequestDTO;
import com.RevHire.entity.Application;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
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

    @PostMapping("/apply")
    public ResponseEntity<String> apply(@RequestParam Long jobId,
                                        @RequestParam Long seekerId,
                                        @RequestParam Long resumeId,
                                        @RequestParam(required = false) String coverLetter) {

        applicationService.applyJob(jobId, seekerId, resumeId, coverLetter);

        return ResponseEntity.ok("Application submitted successfully");
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
}

