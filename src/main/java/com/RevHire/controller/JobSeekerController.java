package com.RevHire.controller;

import com.RevHire.entity.*;
import com.RevHire.service.JobSeekerService;
import com.RevHire.service.ResumeService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

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
    @PostMapping("/profile")
    public ResponseEntity<JobSeekerProfile> createProfile(@RequestBody JobSeekerProfile profile) {
        return ResponseEntity.ok(jobSeekerService.createProfile(profile));
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
    public FavoriteJob addFavorite(@PathVariable Long seekerId, @PathVariable Long jobId) {
        return jobSeekerService.addFavoriteJob(seekerId, jobId);
    }

    @GetMapping("/favorites/{seekerId}")
    public List<FavoriteJob> getFavorites(@PathVariable Long seekerId) {
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