    package com.RevHire.controller;

    import com.RevHire.dto.FavoriteJobDTO;
    import com.RevHire.dto.ProfileUpdateDTO;
    import com.RevHire.entity.*;
    import com.RevHire.repository.*;
    import com.RevHire.service.JobSeekerService;
    import com.RevHire.service.ResumeService;

    import com.RevHire.service.impl.JobSeekerServiceImpl;
    import jakarta.servlet.http.HttpSession;
    import jakarta.transaction.Transactional;
    import org.springframework.http.HttpStatus;
    import org.springframework.http.ResponseEntity;
    import org.springframework.ui.Model;
    import org.springframework.web.bind.annotation.*;
    import org.springframework.web.multipart.MultipartFile;

    import java.util.List;
    import java.util.Map;
    import java.util.Optional;

    @RestController
    @RequestMapping("/api/jobseeker")
    public class JobSeekerController {

        private final JobSeekerService jobSeekerService;
        private final ResumeService resumeService;
        // Mark these as 'final' to ensure they are initialized
        private final ResumeRepository resumeRepo;
        private final FavoriteJobRepository favoriteJobRepo;
        private final JobSeekerProfileRepository profileRepo;
        private final ResumeEducationRepository educationRepo;
        private final ResumeExperienceRepository experienceRepo;

        // Update the constructor to include ALL dependencies
        public JobSeekerController(
                JobSeekerService jobSeekerService,
                ResumeService resumeService,
                ResumeRepository resumeRepo,
                FavoriteJobRepository favoriteJobRepo,
                JobSeekerProfileRepository profileRepo,
                ResumeEducationRepository educationRepo,
                ResumeExperienceRepository experienceRepo) {
            this.jobSeekerService = jobSeekerService;
            this.resumeService = resumeService;
            this.resumeRepo = resumeRepo;
            this.favoriteJobRepo = favoriteJobRepo;
            this.profileRepo = profileRepo;
            this.educationRepo = educationRepo;
            this.experienceRepo = experienceRepo;
        }

        // ========== PROFILE ==========
        @PostMapping("/profile/{userId}")
        public ResponseEntity<JobSeekerProfile> createProfile(
                @PathVariable Long userId,
                @RequestBody JobSeekerProfile profile) {
            return ResponseEntity.ok(jobSeekerService.createProfile(profile, userId));
        }

        @GetMapping("/profile/{userId}")
        public ResponseEntity<?> getProfile(@PathVariable Long userId) {
            return jobSeekerService.getProfile(userId).map(profile -> {
                Optional<Resume> resumeOpt = resumeRepo.findBySeekerSeekerId(profile.getSeekerId());

                java.util.Map<String, Object> response = new java.util.HashMap<>();
                response.put("profileId", profile.getSeekerId());
                response.put("seekerId", profile.getSeekerId());
                response.put("fullName", profile.getFullName());
                response.put("phone", profile.getPhone());
                response.put("location", profile.getLocation());
                response.put("profileCompletion", profile.getProfileCompletion());

                // Ensure these keys match the JS below
                if (resumeOpt.isPresent()) {
                    Resume r = resumeOpt.get();
                    response.put("jobTitle", r.getObjective()); // Maps to 'display-title'
                    response.put("summary", r.getObjective()); // Or a separate summary field if you have one

                    // Map Skills
                    List<ResumeSkill> skillList = resumeService.getSkillsByResume(r.getResumeId());
                    String skillsString = skillList.stream()
                            .map(ResumeSkill::getSkillName)
                            .collect(java.util.stream.Collectors.joining(", "));
                    response.put("skills", skillsString);

                    // Experience Logic
                    response.put("totalExperience", profile.getTotalExperience() != null ? profile.getTotalExperience() : 0);

                    // Education Logic
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

            profileRepo.save(existing);

            return ResponseEntity.ok(existing);
        }

        // ========== RESUME FILE UPLOAD ==========
        @PostMapping("/resume/upload/{userId}")
        public ResponseEntity<?> uploadResume(@PathVariable Long userId, @RequestParam("file") MultipartFile file) {
            try {
                ResumeFile savedFile = jobSeekerService.uploadResumeFile(userId, file);
                return ResponseEntity.ok(Map.of(
                        "message", "Resume uploaded successfully",
                        "fileName", savedFile.getFileName()
                ));
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of("error", e.getMessage()));
            }
        }

        // ========== FAVORITE JOBS ==========
        @PostMapping("/favorites/{seekerId}/{jobId}")
        public ResponseEntity<?> addFavorite(@PathVariable Long seekerId, @PathVariable Long jobId) {
            try {
                // Assuming your service handles the check "if already exists"
                jobSeekerService.addFavoriteJob(seekerId, jobId);

                return ResponseEntity.ok(Map.of(
                        "status", "success",
                        "message", "Job added to favorites successfully",
                        "jobId", jobId
                ));
            } catch (RuntimeException e) {
                // If the service throws an error because it's already saved
                return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                        "status", "error",
                        "message", "This job is already in your saved list."
                ));
            }
        }

        @GetMapping("/favorites/{seekerId}")
        public ResponseEntity<List<FavoriteJobDTO>> getFavorites(@PathVariable Long seekerId) {
            List<FavoriteJobDTO> favorites = jobSeekerService.getFavorites(seekerId);
            return ResponseEntity.ok(favorites);
        }

        @DeleteMapping("/favorites/{favId}")
        public void removeFavorite(@PathVariable Long favId) {
            jobSeekerService.removeFavoriteJob(favId);
        }

        @GetMapping("/jobseeker/jobs/saved")
        public String getSavedJobsPage(HttpSession session, Model model) {
            Long userId = (Long) session.getAttribute("userId");
            if (userId == null) return "redirect:/auth/login";

            // 1. Get Seeker Profile
            JobSeekerProfile profile = profileRepo.findByUserUserId(userId)
                    .orElseThrow(() -> new RuntimeException("Profile not found"));

            // 2. Get Favorites (Assuming your service returns List<FavoriteJobDTO>)
            List<FavoriteJobDTO> savedJobs = jobSeekerService.getFavorites(profile.getSeekerId());

            model.addAttribute("savedJobs", savedJobs);
            model.addAttribute("activePage", "saved");
            return "jobseeker/saved-jobs"; // Your HTML file name
        }


        // ========== NOTIFICATIONS ==========
        @PutMapping("/notifications/{notificationId}/read")
        public void markNotificationAsRead(@PathVariable Long notificationId) {
            jobSeekerService.markNotificationAsRead(notificationId);
        }

        @GetMapping("/api/jobseeker/favorites/count/{userId}")
        @ResponseBody
        public ResponseEntity<Map<String, Long>> getFavoriteCount(@PathVariable Long userId) {
            // Get profile first to get seekerId
            JobSeekerProfile profile = profileRepo.findByUserUserId(userId)
                    .orElseThrow(() -> new RuntimeException("Profile not found"));

            long count = favoriteJobRepo.countBySeekerSeekerId(profile.getSeekerId());
            return ResponseEntity.ok(Map.of("count", count));
        }

        @PostMapping("/resume/save/{userId}")
        @Transactional
        public ResponseEntity<?> saveFullResume(@PathVariable Long userId, @RequestBody Resume resumeData) {
            // 1. Find the profile
            JobSeekerProfile profile = jobSeekerService.getProfile(userId)
                    .orElseThrow(() -> new RuntimeException("Profile not found"));

            // 2. Get or Create the Resume record
            Resume resume = resumeRepo.findBySeekerSeekerId(profile.getSeekerId())
                    .orElseGet(() -> {
                        Resume newR = new Resume();
                        newR.setSeeker(profile);
                        return resumeRepo.save(newR);
                    });

            // 3. Update Objective
            resume.setObjective(resumeData.getObjective());

            // 4. Handle Education (Clear old, add new)
            educationRepo.deleteByResume(resume); // Requires custom query in EducationRepo
            if (resumeData.getEducations() != null) {
                for (ResumeEducation edu : resumeData.getEducations()) {
                    edu.setResume(resume);
                    educationRepo.save(edu);
                }
            }

            // 5. Handle Experience (Clear old, add new)
            experienceRepo.deleteByResume(resume); // Requires custom query in ExperienceRepo
            if (resumeData.getExperiences() != null) {
                for (ResumeExperience exp : resumeData.getExperiences()) {
                    exp.setResume(resume);
                    experienceRepo.save(exp);
                }
            }

            return ResponseEntity.ok(Map.of("message", "Resume saved successfully"));
        }

    }