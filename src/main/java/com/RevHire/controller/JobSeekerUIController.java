package com.RevHire.controller;

import com.RevHire.dto.ApplicationResponseDTO;
import com.RevHire.dto.FavoriteJobDTO;
import com.RevHire.dto.JobDTO;
import com.RevHire.dto.ProfileUpdateDTO;
import com.RevHire.entity.JobSeekerProfile;
import com.RevHire.entity.Resume;
import com.RevHire.entity.ResumeSkill;
import com.RevHire.entity.User;
import com.RevHire.repository.JobSeekerProfileRepository;
import com.RevHire.repository.ResumeRepository;
import com.RevHire.repository.ResumeSkillRepository;
import com.RevHire.repository.UserRepository;
import com.RevHire.service.ApplicationService;
import com.RevHire.service.JobSeekerService;
import com.RevHire.service.ResumeService;
import com.RevHire.service.impl.JobServiceImpl;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@Controller // This is the key! It allows returning HTML templates.
@RequestMapping("/jobseeker") // Matches the redirect in AuthController
public class JobSeekerUIController {

    private final JobSeekerService jobSeekerService;
    private final ResumeService resumeService;
    private final ResumeRepository resumeRepo;
    private JobServiceImpl jobService;
    private ApplicationService applicationService;
    private final UserRepository userRepository;
    private final ResumeSkillRepository resumeSkillRepo;
    private final JobSeekerProfileRepository profileRepo;

    // Update your constructor like this:
    public JobSeekerUIController(JobSeekerService jobSeekerService,
                                 ResumeService resumeService,
                                 ResumeRepository resumeRepo, // <--- Ensure this is here
                                 JobServiceImpl jobService,
                                 ApplicationService applicationService,
                                 UserRepository userRepository,
                                 ResumeSkillRepository resumeSkillRepo,
                                 JobSeekerProfileRepository profileRepo) {
        this.jobSeekerService = jobSeekerService;
        this.resumeService = resumeService;
        this.resumeRepo = resumeRepo; // <--- And assigned here
        this.jobService = jobService;
        this.applicationService = applicationService;
        this.userRepository = userRepository;
        this.resumeSkillRepo = resumeSkillRepo;
        this.profileRepo = profileRepo;
    }

    // ================= DASHBOARD =================
    @GetMapping("/dashboard/{userId}")
    public String dashboard(@PathVariable Long userId, Model model, HttpSession session) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        JobSeekerProfile profile = jobSeekerService.getProfile(userId).orElse(null);

        String displayName = "Seeker";
        int score = 0;

        if (profile != null) {
            displayName = (profile.getFullName() != null && !profile.getFullName().trim().isEmpty())
                    ? profile.getFullName() : "Seeker";
            score = profile.getProfileCompletion() != null ? profile.getProfileCompletion() : 0;
        }

        session.setAttribute("userId", user.getUserId());
        session.setAttribute("userName", displayName);
        session.setAttribute("userInitial", displayName.substring(0, 1).toUpperCase());
        session.setAttribute("userEmail", user.getEmail());   // 🔥 ADD THIS LINE

        model.addAttribute("userId", userId);
        model.addAttribute("userName", displayName);
        model.addAttribute("profileScore", score);
        model.addAttribute("activePage", "dashboard");

        return "jobseeker/dashboard";
    }

    @GetMapping("/profile/manage")
    public String manageProfile(HttpSession session,Model model) {
        // Security check: ensure user is logged in
        if (session.getAttribute("userId") == null) return "redirect:/auth/login";
        model.addAttribute("activePage", "profile");
        return "jobseeker/profile"; // Matches src/main/resources/templates/jobseeker/profile.html
    }

    @GetMapping("/edit-profile")
    public String editProfile(HttpSession session) {
        if (session.getAttribute("userId") == null) return "redirect:/auth/login";
        return "jobseeker/edit-profile"; // Displays the edit form
    }

    @GetMapping("/resume/builder")
    public String showResumeBuilder(HttpSession session,Model model) {
        if (session.getAttribute("userId") == null) return "redirect:/auth/login";
        model.addAttribute("activePage", "builder");

        return "jobseeker/resume-builder"; // Points to templates/jobseeker/resume-builder.html
    }

    @GetMapping("/resume/upload")
    public String showUploadPage(HttpSession session,Model model) {
        // Simple security check: redirect to login if session is empty
        if (session.getAttribute("userId") == null) {
            return "redirect:/auth/login";
        }
        model.addAttribute("activePage", "upload");
        // This returns the 'resume-upload.html' file from src/main/resources/templates/jobseeker/
        return "jobseeker/resume-upload";
    }

    @GetMapping("/resume/view")
    public String viewResume(HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return "redirect:/auth/login";

        // 1. Get the profile using the JobSeekerService
        jobSeekerService.getProfile(userId).ifPresent(profile -> {
            model.addAttribute("profile", profile);

            // 2. Find the Resume associated with the seeker
            Optional<Resume> resumeOpt = resumeRepo.findBySeekerSeekerId(profile.getSeekerId());

            resumeOpt.ifPresent(resume -> {
                // 3. Populate lists using the ResumeService logic from your ResumeController
                Long rId = resume.getResumeId();
                model.addAttribute("resume", resume);
                model.addAttribute("educations", resumeService.getEducationByResume(rId));
                model.addAttribute("experiences", resumeService.getExperienceByResume(rId));
                model.addAttribute("skills", resumeService.getSkillsByResume(rId));
            });
        });

        return "jobseeker/view-resume";
    }

    @GetMapping("/jobs/search")
    public String showSearchPage(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) Integer experience,
            @RequestParam(required = false) String education,
            @RequestParam(required = false) Double minSalary,
            @RequestParam(required = false) Double maxSalary,
            @RequestParam(required = false) String jobType,
            HttpSession session,
            Model model) {

        if (session.getAttribute("userId") == null) return "redirect:/auth/login";

        // Use all 7 parameters. If null, Service/Repo should handle them as wildcards.
        List<JobDTO> filteredJobs = jobService.searchJobs(
                title, location, experience, education, minSalary, maxSalary, jobType
        );

        model.addAttribute("jobs", filteredJobs);
        model.addAttribute("activePage", "search");
        return "jobseeker/search-jobs";
    }

    @GetMapping("/applications")
    public String showApplicationsPage(HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return "redirect:/auth/login";

        // We fetch the profile first to get the seekerId needed for the application service
        jobSeekerService.getProfile(userId).ifPresent(profile -> {
            List<ApplicationResponseDTO> apps = applicationService.getApplicationsBySeeker(profile.getSeekerId());
            model.addAttribute("applications", apps);
            model.addAttribute("activePage", "applications");
        });

        return "jobseeker/applications"; // Points to a new applications.html or the dashboard
    }

    @GetMapping("/jobs/saved")
    public String showSavedJobs(HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return "redirect:/auth/login";

        // 1. Get the seeker profile to find the seekerId
        jobSeekerService.getProfile(userId).ifPresent(profile -> {
            // 2. Fetch the DTO list from your service
            List<FavoriteJobDTO> favorites = jobSeekerService.getFavorites(profile.getSeekerId());
            model.addAttribute("savedJobs", favorites);
            model.addAttribute("activePage", "saved");
        });

        return "jobseeker/saved-jobs"; // Points to the new HTML file
    }

    @Transactional
    public void updateFullProfile(Long userId, ProfileUpdateDTO dto) {
        // 1. Update Profile (Basic Info)
        JobSeekerProfile profile = profileRepo.findByUserUserId(userId)
                .orElseThrow(() -> new RuntimeException("Profile not found"));

        profile.setFullName(dto.getFullName());
        profile.setPhone(dto.getPhone());
        profile.setLocation(dto.getLocation());
        profileRepo.save(profile);

        // 2. Get or Create Resume
        Resume resume = resumeRepo.findBySeekerSeekerId(profile.getSeekerId())
                .orElseGet(() -> {
                    Resume newR = new Resume();
                    newR.setSeeker(profile);
                    return resumeRepo.save(newR);
                });

        // 3. Update Objective (Job Title)
        resume.setObjective(dto.getJobTitle());
        resumeRepo.save(resume);

        // 4. Update Skills (Relational Table)
        // Delete existing skills first to avoid duplicates
        resumeSkillRepo.deleteByResume(resume);

        if (dto.getSkills() != null && !dto.getSkills().trim().isEmpty()) {
            String[] skillNames = dto.getSkills().split(",");
            for (String name : skillNames) {
                if (!name.trim().isEmpty()) {
                    ResumeSkill rs = new ResumeSkill();
                    rs.setResume(resume);
                    rs.setSkillName(name.trim());
                    resumeSkillRepo.save(rs);
                }
            }
        }

        // Update Completion Percentage
        int completion = calculateCompletion(profile, resume);
        profile.setProfileCompletion(completion);
        profileRepo.save(profile);
    }

    private int calculateCompletion(JobSeekerProfile p, Resume r) {
        int count = 0;
        if(p.getFullName() != null) count += 20;
        if(p.getPhone() != null) count += 20;
        if(p.getLocation() != null) count += 20;
        if(r.getObjective() != null) count += 20;
        // Check if skills exist
        if(!resumeSkillRepo.findByResume(r).isEmpty()) count += 20;
        return count;
    }



}