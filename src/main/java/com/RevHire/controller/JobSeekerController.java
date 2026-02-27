package com.RevHire.controller;

import com.RevHire.dto.FavoriteJobDTO;
import com.RevHire.entity.*;
import com.RevHire.service.JobSeekerService;
import com.RevHire.service.ResumeService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/jobseeker")
public class JobSeekerController {

    private final JobSeekerService jobSeekerService;
    private final ResumeService resumeService;

    public JobSeekerController(JobSeekerService jobSeekerService, ResumeService resumeService) {
        this.jobSeekerService = jobSeekerService;
        this.resumeService = resumeService;
    }
    // ========== PROFILE ==========
    @PostMapping("/profile/{userId}")
    public ResponseEntity<JobSeekerProfile> createProfile(
            @PathVariable Long userId,
            @RequestBody JobSeekerProfile profile) {
        return ResponseEntity.ok(jobSeekerService.createProfile(profile, userId));
    }

    @GetMapping("/profile/{userId}")
    public ResponseEntity<JobSeekerProfile> getProfile(@PathVariable Long userId) {
        return jobSeekerService.getProfile(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/profile/{profileId}")
    public ResponseEntity<JobSeekerProfile> updateProfile(@PathVariable Long profileId,
                                                          @RequestBody JobSeekerProfile profile) {
        return ResponseEntity.ok(jobSeekerService.updateProfile(profileId, profile));
    }

    // ========== RESUME FILE UPLOAD ==========
    @PostMapping("/resume/{resumeId}/upload")
    public ResumeFile uploadResume(@PathVariable Long resumeId,
                                   @RequestParam("file") MultipartFile file) {
        return jobSeekerService.uploadResumeFile(resumeId, file);
    }

    // ========== FAVORITE JOBS ==========
    @PostMapping("/favorites/{seekerId}/{jobId}")
    public ResponseEntity<?> addFavorite(@PathVariable Long seekerId, @PathVariable Long jobId) {
        jobSeekerService.addFavoriteJob(seekerId, jobId);

        return ResponseEntity.ok(Map.of(
                "message", "Job added to favorites successfully",
                "jobId", jobId
        ));
    }

    @GetMapping("/favorites/{seekerId}")
    public List<FavoriteJobDTO> getFavorites(@PathVariable Long seekerId) {
        return jobSeekerService.getFavorites(seekerId);
    }

    @DeleteMapping("/favorites/{favId}")
    public void removeFavorite(@PathVariable Long favId) {

        jobSeekerService.removeFavoriteJob(favId);
    }

    // ========== NOTIFICATIONS ==========
    @PutMapping("/notifications/{notificationId}/read")
    public void markNotificationAsRead(@PathVariable Long notificationId) {
        jobSeekerService.markNotificationAsRead(notificationId);
    }
}