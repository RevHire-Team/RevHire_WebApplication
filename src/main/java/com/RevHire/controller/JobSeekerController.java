package com.RevHire.controller;

import com.RevHire.dto.ApplicationResponseDTO;
import com.RevHire.dto.FavoriteJobDTO;
import com.RevHire.entity.*;
import com.RevHire.repository.*;
import com.RevHire.service.JobSeekerService;
import com.RevHire.service.ResumeService;
import com.RevHire.service.impl.ApplicationServiceImpl;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    // --- PROFILE METHODS (UNCHANGED) ---
    @PostMapping("/profile/{userId}")
    public ResponseEntity<JobSeekerProfile> createProfile(@PathVariable Long userId, @RequestBody JobSeekerProfile profile) {
        return ResponseEntity.ok(jobSeekerService.createProfile(profile, userId));
    }

    @GetMapping("/profile/{userId}")
    public ResponseEntity<?> getProfile(@PathVariable Long userId) {
        return jobSeekerService.getProfile(userId).map(profile -> {
            Optional<Resume> resumeOpt = resumeRepo.findBySeekerSeekerId(profile.getSeekerId());
            Map<String, Object> response = new HashMap<>();
            response.put("profileId", profile.getSeekerId());
            response.put("fullName", profile.getFullName());
            response.put("phone", profile.getPhone());
            response.put("location", profile.getLocation());
            response.put("profileCompletion", profile.getProfileCompletion());
            response.put("totalExperience", profile.getTotalExperience());

            if (resumeOpt.isPresent()) {
                Resume r = resumeOpt.get();
                response.put("objective", r.getObjective());
                response.put("skills", resumeService.getSkillsByResume(r.getResumeId()));
                response.put("education", educationRepo.findByResumeResumeId(r.getResumeId()));
                response.put("experience", experienceRepo.findByResumeResumeId(r.getResumeId()));
                response.put("certifications", certificationRepo.findByResumeResumeId(r.getResumeId()));
                response.put("projects", projectRepo.findByResumeResumeId(r.getResumeId()));
            }
            return ResponseEntity.ok(response);
        }).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/profile/{userId}")
    public ResponseEntity<?> updateProfile(@PathVariable Long userId, @RequestBody Map<String,Object> data) {
        JobSeekerProfile profile = profileRepo.findByUserUserId(userId).orElseThrow(() -> new RuntimeException("Profile not found"));
        profile.setFullName((String) data.get("fullName"));
        profile.setPhone((String) data.get("phone"));
        profile.setLocation((String) data.get("location"));
        if(data.get("employmentStatus") != null) profile.setTotalExperience(parseExperience((String) data.get("employmentStatus")));
        profileRepo.save(profile);

        Resume resume = resumeRepo.findTopBySeekerSeekerIdOrderByResumeIdDesc(profile.getSeekerId()).orElseGet(() -> {
            Resume r = new Resume(); r.setSeeker(profile); return resumeRepo.save(r);
        });
        resume.setObjective((String) data.get("experience"));
        resumeRepo.save(resume);

        Long resumeId = resume.getResumeId();
        resumeSkillRepo.deleteByResumeResumeId(resumeId);
        String skillsStr = (String) data.get("skills");
        if(skillsStr != null) {
            for(String s : skillsStr.split(",")) {
                ResumeSkill skill = new ResumeSkill(); skill.setResume(resume); skill.setSkillName(s.trim()); resumeSkillRepo.save(skill);
            }
        }
        return ResponseEntity.ok(Map.of("message","Profile updated successfully"));
    }

    // --- RESUME UPLOAD (UNCHANGED) ---
    @PostMapping("/resume/upload/{userId}")
    public ResponseEntity<?> uploadResume(@PathVariable Long userId, @RequestParam("file") MultipartFile file) {
        try {
            if (file.getSize() > 2 * 1024 * 1024) return ResponseEntity.badRequest().body(Map.of("error", "File size must be less than 2MB"));
            ResumeFile savedFile = jobSeekerService.uploadResumeFile(userId, file);
            return ResponseEntity.ok(Map.of("message", "Resume uploaded successfully", "fileName", savedFile.getFileName()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }

    // --- FAVORITE JOBS ---
    @PostMapping("/favorites/{seekerId}/{jobId}")
    public ResponseEntity<?> addFavorite(@PathVariable Long seekerId, @PathVariable Long jobId) {
        try {
            jobSeekerService.addFavoriteJob(seekerId, jobId);
            return ResponseEntity.ok(Map.of("status", "success", "message", "Job added to favorites", "jobId", jobId));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("status", "error", "message", "Already saved."));
        }
    }

    @GetMapping("/favorites/{seekerId}")
    public ResponseEntity<List<FavoriteJobDTO>> getFavorites(@PathVariable Long seekerId) {
        return ResponseEntity.ok(jobSeekerService.getFavorites(seekerId));
    }

    // UPDATED: Now accepts seekerId and jobId to match your frontend script
    @DeleteMapping("/favorites/{seekerId}/{jobId}")
    public ResponseEntity<?> removeFavorite(@PathVariable Long seekerId, @PathVariable Long jobId) {
        logger.info("Request to remove favorite job {} for seeker {}", jobId, seekerId);
        try {
            // Finding the favorite record using the IDs passed from the UI
            Optional<FavoriteJob> favorite = favoriteJobRepo.findBySeekerSeekerIdAndJobJobId(seekerId, jobId);
            if (favorite.isPresent()) {
                favoriteJobRepo.delete(favorite.get());
                return ResponseEntity.ok(Map.of("message", "Job removed successfully"));
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Job not found in saved list"));
        } catch (Exception e) {
            logger.error("Error removing favorite", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Could not remove job"));
        }
    }

    // --- DASHBOARD (UNCHANGED) ---
    @GetMapping("/dashboard/{userId}")
    public ResponseEntity<?> getDashboard(@PathVariable Long userId) {
        JobSeekerProfile profile = profileRepo.findByUserUserId(userId).orElseThrow(() -> new RuntimeException("Profile not found"));
        Long seekerId = profile.getSeekerId();
        long savedJobsCount = favoriteJobRepo.countBySeekerSeekerId(seekerId);
        List<ApplicationResponseDTO> applications = applicationService.getApplicationsBySeeker(seekerId);
        return ResponseEntity.ok(Map.of(
                "profileScore", profile.getProfileCompletion() != null ? profile.getProfileCompletion() : 0,
                "totalApplications", applications.size(),
                "savedJobs", savedJobsCount,
                "recentApplications", applications.stream().limit(5).toList()
        ));
    }

    private int parseExperience(String status){
        switch(status){
            case "Fresher": return 0;
            case "0-2 Years Experience": return 1;
            case "3-5 Years Experience": return 4;
            case "6-10 Years Experience": return 8;
            case "10+ Years Experience": return 12;
            default: return 0;
        }
    }
}