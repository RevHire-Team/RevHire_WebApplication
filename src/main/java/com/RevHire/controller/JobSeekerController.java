package com.RevHire.controller;

import com.RevHire.dto.ApplicationResponseDTO;
import com.RevHire.dto.FavoriteJobDTO;
import com.RevHire.entity.*;
import com.RevHire.repository.*;
import com.RevHire.service.JobSeekerService;
import com.RevHire.service.ResumeService;
import com.RevHire.service.impl.ApplicationServiceImpl;

import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@RestController
@RequestMapping("/api/jobseeker")
public class JobSeekerController {

    private final JobSeekerService jobSeekerService;
    private final ResumeService resumeService;
    private final ApplicationServiceImpl applicationService;

    private final ResumeRepository resumeRepo;
    private final FavoriteJobRepository favoriteJobRepo;
    private final JobSeekerProfileRepository profileRepo;
    private final ResumeEducationRepository educationRepo;
    private final ResumeExperienceRepository experienceRepo;

    public JobSeekerController(
            JobSeekerService jobSeekerService,
            ResumeService resumeService,
            ResumeRepository resumeRepo,
            FavoriteJobRepository favoriteJobRepo,
            JobSeekerProfileRepository profileRepo,
            ResumeEducationRepository educationRepo,
            ResumeExperienceRepository experienceRepo,
            ApplicationServiceImpl applicationService) {

        this.jobSeekerService = jobSeekerService;
        this.resumeService = resumeService;
        this.resumeRepo = resumeRepo;
        this.favoriteJobRepo = favoriteJobRepo;
        this.profileRepo = profileRepo;
        this.educationRepo = educationRepo;
        this.experienceRepo = experienceRepo;
        this.applicationService = applicationService;
    }

    /* ================= PROFILE ================= */

    @PostMapping("/profile/{userId}")
    public ResponseEntity<JobSeekerProfile> createProfile(
            @PathVariable Long userId,
            @RequestBody JobSeekerProfile profile) {

        return ResponseEntity.ok(jobSeekerService.createProfile(profile, userId));
    }


    @GetMapping("/profile/{userId}")
    public ResponseEntity<?> getProfile(@PathVariable Long userId) {

        return jobSeekerService.getProfile(userId).map(profile -> {

            Optional<Resume> resumeOpt =
                    resumeRepo.findBySeekerSeekerId(profile.getSeekerId());

            Map<String, Object> response = new HashMap<>();

            response.put("profileId", profile.getSeekerId());
            response.put("seekerId", profile.getSeekerId());
            response.put("fullName", profile.getFullName());
            response.put("phone", profile.getPhone());
            response.put("location", profile.getLocation());
            response.put("profileCompletion", profile.getProfileCompletion());
            response.put("totalExperience", profile.getTotalExperience());

            if (resumeOpt.isPresent()) {

                Resume r = resumeOpt.get();

                response.put("jobTitle", r.getObjective());
                response.put("summary", r.getObjective());

                /* ===== SKILLS FROM ResumeSkill TABLE ===== */

                List<ResumeSkill> skillList =
                        resumeService.getSkillsByResume(r.getResumeId());

                String skillsString = skillList.stream()
                        .map(ResumeSkill::getSkillName)
                        .reduce("", (a,b) -> a.isEmpty()?b:a + "," + b);

                response.put("skills", skillsString);

                /* ===== CERTIFICATIONS PLACEHOLDER ===== */

                response.put("certifications", "");

                /* ===== EDUCATION ===== */

                var eduList = resumeService.getEducationByResume(r.getResumeId());

                if (!eduList.isEmpty()) {

                    ResumeEducation edu = eduList.get(0);

                    String degree = edu.getDegree() != null ? edu.getDegree() : "";
                    String institution = edu.getInstitution() != null ? edu.getInstitution() : "";
                    String year = edu.getYearOfCompletion() != null ?
                            String.valueOf(edu.getYearOfCompletion()) : "";

                    String educationText = degree;

                    if (!institution.isEmpty()) {
                        educationText += " - " + institution;
                    }

                    if (!year.isEmpty()) {
                        educationText += " (" + year + ")";
                    }

                    response.put("education", educationText);
                }
            }

            return ResponseEntity.ok(response);

        }).orElse(ResponseEntity.notFound().build());
    }


    @PutMapping("/profile/{userId}")
    public ResponseEntity<?> updateByUserId(
            @PathVariable Long userId,
            @RequestBody JobSeekerProfile profile) {

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


    /* ================= RESUME FILE UPLOAD ================= */

    @PostMapping("/resume/upload/{userId}")
    public ResponseEntity<?> uploadResume(
            @PathVariable Long userId,
            @RequestParam("file") MultipartFile file) {

        try {

            ResumeFile savedFile =
                    jobSeekerService.uploadResumeFile(userId, file);

            return ResponseEntity.ok(Map.of(
                    "message", "Resume uploaded successfully",
                    "fileName", savedFile.getFileName()
            ));

        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }


    /* ================= FAVORITE JOBS ================= */

    @PostMapping("/favorites/{seekerId}/{jobId}")
    public ResponseEntity<?> addFavorite(
            @PathVariable Long seekerId,
            @PathVariable Long jobId) {

        jobSeekerService.addFavoriteJob(seekerId, jobId);

        return ResponseEntity.ok(Map.of(
                "message", "Job added to favorites"
        ));
    }

    @GetMapping("/favorites/{seekerId}")
    public ResponseEntity<List<FavoriteJobDTO>> getFavorites(
            @PathVariable Long seekerId) {

        List<FavoriteJobDTO> favorites =
                jobSeekerService.getFavorites(seekerId);

        return ResponseEntity.ok(favorites);
    }

    @DeleteMapping("/favorites/{favId}")
    public void removeFavorite(@PathVariable Long favId) {

        jobSeekerService.removeFavoriteJob(favId);
    }


    /* ================= DASHBOARD ================= */

    @GetMapping("/dashboard/{userId}")
    public ResponseEntity<?> getDashboard(@PathVariable Long userId) {

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