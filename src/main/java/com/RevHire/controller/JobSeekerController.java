package com.RevHire.controller;

import com.RevHire.dto.ApplicationResponseDTO;
import com.RevHire.dto.FavoriteJobDTO;
import com.RevHire.dto.ProfileUpdateDTO;
import com.RevHire.entity.*;
import com.RevHire.repository.*;
import com.RevHire.service.JobSeekerService;
import com.RevHire.service.ResumeService;
import com.RevHire.service.impl.ApplicationServiceImpl;

import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@RestController
@RequestMapping("/api/jobseeker")
public class JobSeekerController {

    private static final Logger logger = LogManager.getLogger(JobSeekerController.class);

    private final JobSeekerService jobSeekerService;
    private final ResumeService resumeService;
    private final ApplicationServiceImpl applicationService;
    private final ResumeRepository resumeRepo;
    private final FavoriteJobRepository favoriteJobRepo;
    private final JobSeekerProfileRepository profileRepo;
    private final ResumeEducationRepository educationRepo;
    private final ResumeExperienceRepository experienceRepo;
    private final ResumeCertificationRepository certificationRepo;
    private final ResumeProjectRepository projectRepo;
    private final ResumeSkillRepository resumeSkillRepo;

    public JobSeekerController(
            JobSeekerService jobSeekerService,
            ResumeService resumeService,
            ResumeRepository resumeRepo,
            FavoriteJobRepository favoriteJobRepo,
            JobSeekerProfileRepository profileRepo,
            ResumeEducationRepository educationRepo,
            ResumeExperienceRepository experienceRepo,
            ApplicationServiceImpl applicationService,
            ResumeCertificationRepository certificationRepo,
            ResumeProjectRepository projectRepo,
            ResumeSkillRepository resumeSkillRepo) {

        this.jobSeekerService = jobSeekerService;
        this.resumeService = resumeService;
        this.resumeRepo = resumeRepo;
        this.favoriteJobRepo = favoriteJobRepo;
        this.profileRepo = profileRepo;
        this.educationRepo = educationRepo;
        this.experienceRepo = experienceRepo;
        this.applicationService = applicationService;
        this.certificationRepo = certificationRepo;
        this.projectRepo = projectRepo;
        this.resumeSkillRepo = resumeSkillRepo;
    }

    // PROFILE
    @PostMapping("/profile/{userId}")
    public ResponseEntity<JobSeekerProfile> createProfile(
            @PathVariable Long userId,
            @RequestBody JobSeekerProfile profile) {

        logger.info("Creating profile for userId: {}", userId);

        return ResponseEntity.ok(jobSeekerService.createProfile(profile, userId));
    }

    @GetMapping("/profile/{userId}")
    public ResponseEntity<?> getProfile(@PathVariable Long userId) {

        logger.info("Fetching profile for userId: {}", userId);

        return jobSeekerService.getProfile(userId).map(profile -> {

            Optional<Resume> resumeOpt =
                    resumeRepo.findBySeekerSeekerId(profile.getSeekerId());

            Map<String, Object> response = new HashMap<>();

            response.put("profileId", profile.getSeekerId());
            response.put("fullName", profile.getFullName());
            response.put("phone", profile.getPhone());
            response.put("location", profile.getLocation());
            response.put("profileCompletion", profile.getProfileCompletion());
            response.put("totalExperience", profile.getTotalExperience());

            if (resumeOpt.isPresent()) {

                Resume r = resumeOpt.get();

                logger.debug("Resume found for seekerId {}", profile.getSeekerId());

                response.put("objective", r.getObjective());

                List<ResumeSkill> skillList =
                        resumeService.getSkillsByResume(r.getResumeId());

                response.put("skills", skillList);

                response.put("education",
                        educationRepo.findByResumeResumeId(r.getResumeId()));

                response.put("experience",
                        experienceRepo.findByResumeResumeId(r.getResumeId()));

                response.put("certifications",
                        certificationRepo.findByResumeResumeId(r.getResumeId()));

                response.put("projects",
                        projectRepo.findByResumeResumeId(r.getResumeId()));
            }

            return ResponseEntity.ok(response);

        }).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/profile/{userId}")
    public ResponseEntity<?> updateProfile(
            @PathVariable Long userId,
            @RequestBody JobSeekerProfile profile) {

        logger.info("Updating profile for userId {}", userId);

        JobSeekerProfile existing =
                profileRepo.findByUserUserId(userId)
                        .orElseThrow(() -> new RuntimeException("Profile not found"));

        existing.setFullName(profile.getFullName());
        existing.setPhone(profile.getPhone());
        existing.setLocation(profile.getLocation());
        existing.setTotalExperience(profile.getTotalExperience());

        profileRepo.save(existing);

        return ResponseEntity.ok(existing);
    }

    // RESUME FILE UPLOAD
    @PostMapping("/resume/upload/{userId}")
    public ResponseEntity<?> uploadResume(
            @PathVariable Long userId,
            @RequestParam("file") MultipartFile file) {

        logger.info("Uploading resume for userId {}", userId);

        try {

            if (file.getSize() > 2 * 1024 * 1024) {

                logger.warn("File size exceeds limit for userId {}", userId);

                return ResponseEntity.badRequest()
                        .body(Map.of("error", "File size must be less than 2MB"));
            }

            ResumeFile savedFile =
                    jobSeekerService.uploadResumeFile(userId, file);

            logger.info("Resume uploaded successfully: {}", savedFile.getFileName());

            return ResponseEntity.ok(Map.of(
                    "message", "Resume uploaded successfully",
                    "fileName", savedFile.getFileName()
            ));

        } catch (Exception e) {

            logger.error("Resume upload failed for userId {}", userId, e);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // FAVORITE JOBS
    @PostMapping("/favorites/{seekerId}/{jobId}")
    public ResponseEntity<?> addFavorite(@PathVariable Long seekerId, @PathVariable Long jobId) {

        logger.info("Adding favorite job {} for seeker {}", jobId, seekerId);

        try {

            jobSeekerService.addFavoriteJob(seekerId, jobId);

            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "Job added to favorites successfully",
                    "jobId", jobId
            ));

        } catch (RuntimeException e) {

            logger.warn("Job already saved: jobId {}", jobId);

            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                    "status", "error",
                    "message", "This job is already in your saved list."
            ));
        }
    }

    @GetMapping("/favorites/{seekerId}")
    public ResponseEntity<List<FavoriteJobDTO>> getFavorites(@PathVariable Long seekerId) {

        logger.info("Fetching favorite jobs for seekerId {}", seekerId);

        List<FavoriteJobDTO> favorites = jobSeekerService.getFavorites(seekerId);

        return ResponseEntity.ok(favorites);
    }

    @DeleteMapping("/favorites/{favId}")
    public void removeFavorite(@PathVariable Long favId) {

        logger.warn("Removing favorite job with id {}", favId);

        jobSeekerService.removeFavoriteJob(favId);
    }

    // DASHBOARD
    @GetMapping("/dashboard/{userId}")
    public ResponseEntity<?> getDashboard(@PathVariable Long userId) {

        logger.info("Loading dashboard for userId {}", userId);

        JobSeekerProfile profile =
                profileRepo.findByUserUserId(userId)
                        .orElseThrow(() -> new RuntimeException("Profile not found"));

        Long seekerId = profile.getSeekerId();

        long savedJobs =
                favoriteJobRepo.countBySeekerSeekerId(seekerId);

        List<ApplicationResponseDTO> applications =
                applicationService.getApplicationsBySeeker(seekerId);

        int totalApps = applications.size();

        List<ApplicationResponseDTO> recent =
                applications.stream().limit(5).toList();

        return ResponseEntity.ok(Map.of(
                "profileScore",
                profile.getProfileCompletion() != null ?
                        profile.getProfileCompletion() : 0,
                "totalApplications", totalApps,
                "savedJobs", savedJobs,
                "recentApplications", recent
        ));
    }
}