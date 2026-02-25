package com.RevHire.controller;

import com.RevHire.dto.ApplicationResponseDTO;
import com.RevHire.entity.Application;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.RevHire.service.ApplicationService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
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
    public ResponseEntity<List<ApplicationResponseDTO>> viewSeekerApplications(@PathVariable Long seekerId) {

        return ResponseEntity.ok(
                applicationService.getApplicationsBySeeker(seekerId)
        );
    }

    @PostMapping("/withdraw/{id}")
    public ResponseEntity<String> withdraw(@PathVariable Long id, @RequestParam String reason) {

        applicationService.withdrawApplication(id, reason);
        return ResponseEntity.ok("Application withdrawn successfully");
    }

    @PostMapping("/update-status/{id}")
    public ResponseEntity<String> updateStatus(@PathVariable Long id, @RequestParam String status) {

        applicationService.updateStatus(id, status);
//        return "redirect:/applications";
        return ResponseEntity.ok("Application withdrawn successfully");
    }
}