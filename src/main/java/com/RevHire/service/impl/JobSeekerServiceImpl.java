package com.RevHire.service.impl;

import com.RevHire.dto.FavoriteJobDTO;
import com.RevHire.dto.JobDTO;
import com.RevHire.entity.*;
import com.RevHire.repository.*;
import com.RevHire.service.JobSeekerService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class JobSeekerServiceImpl implements JobSeekerService {

    private final JobSeekerProfileRepository profileRepo;
    private final ResumeRepository resumeRepo;
    private final ResumeFileRepository resumeFileRepo;
    private final FavoriteJobRepository favoriteJobRepo;
    private final NotificationRepository notificationRepo;
    private final UserRepository userRepo;
    private final JobRepository jobRepo;
    private final ResumeSkillRepository resumeSkillRepo;

    public JobSeekerServiceImpl(JobSeekerProfileRepository profileRepo,
                                ResumeRepository resumeRepo,
                                ResumeFileRepository resumeFileRepo,
                                FavoriteJobRepository favoriteJobRepo,
                                NotificationRepository notificationRepo,
                                UserRepository userRepo,
                                JobRepository jobRepo,
                                ResumeSkillRepository resumeSkillRepo) {
        this.profileRepo = profileRepo;
        this.resumeRepo = resumeRepo;
        this.resumeFileRepo = resumeFileRepo;
        this.favoriteJobRepo = favoriteJobRepo;
        this.notificationRepo = notificationRepo;
        this.userRepo = userRepo;
        this.jobRepo = jobRepo;
        this.resumeSkillRepo = resumeSkillRepo;
    }

    // ========================= PROFILE =========================

    @Override
    public JobSeekerProfile createProfile(JobSeekerProfile profile, Long userId) {

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        profile.setUser(user);
        return profileRepo.save(profile);
    }

    @Override
    public Optional<JobSeekerProfile> getProfile(Long userId) {
        // Return null instead of Optional to avoid JSON issues
        return Optional.ofNullable(profileRepo.findByUserUserId(userId)
                .orElse(null));
    }

    @Override
    public JobSeekerProfile updateProfile(Long profileId, JobSeekerProfile profile) {

        JobSeekerProfile existing = profileRepo.findById(profileId)
                .orElseThrow(() -> new RuntimeException("Profile not found"));

        existing.setFullName(profile.getFullName());
        existing.setPhone(profile.getPhone());
        existing.setLocation(profile.getLocation());
        existing.setCurrentEmploymentStatus(profile.getCurrentEmploymentStatus());
        existing.setTotalExperience(profile.getTotalExperience());
        existing.setProfileCompletion(profile.getProfileCompletion());

        return profileRepo.save(existing);
    }

    // ========================= RESUME =========================

    @Override
    public Resume getOrCreateResume(Long userId) {

        JobSeekerProfile profile = profileRepo.findByUserUserId(userId)
                .orElseThrow(() -> new RuntimeException("Profile not found. Create profile first."));

        return resumeRepo.findBySeekerSeekerId(profile.getSeekerId())
                .orElseGet(() -> {
                    Resume newResume = new Resume();
                    newResume.setSeeker(profile);
                    return resumeRepo.save(newResume);
                });
    }

    @Override
    public ResumeFile uploadResumeFile(Long userId, MultipartFile file) {

        JobSeekerProfile profile = profileRepo.findByUserUserId(userId)
                .orElseThrow(() -> new RuntimeException("Profile not found"));

        Resume resume = getOrCreateResume(userId);

        try {

            String projectPath = System.getProperty("user.dir");
            String uploadDir = projectPath + File.separator + "uploads" + File.separator + "resumes";

            File folder = new File(uploadDir);
            if (!folder.exists()) folder.mkdirs();

            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            File destination = new File(uploadDir + File.separator + fileName);
            file.transferTo(destination);

            ResumeFile resumeFile = new ResumeFile();
            resumeFile.setResume(resume);
            resumeFile.setFileName(fileName);
            resumeFile.setFilePath(destination.getAbsolutePath());
            resumeFile.setFileSize(file.getSize());

            String originalName = file.getOriginalFilename();
            String extension = (originalName != null && originalName.contains("."))
                    ? originalName.substring(originalName.lastIndexOf(".") + 1).toUpperCase()
                    : "UNKNOWN";

            resumeFile.setFileType(extension);

            return resumeFileRepo.save(resumeFile);

        } catch (IOException e) {
            throw new RuntimeException("FileSystem Error: Could not save file", e);
        }
    }

    // ========================= FAVORITES =========================

    @Override
    public FavoriteJob addFavoriteJob(Long seekerId, Long jobId) {

        JobSeekerProfile seeker = profileRepo.findById(seekerId)
                .orElseThrow(() -> new RuntimeException("Seeker not found"));

        Job job = jobRepo.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        if (favoriteJobRepo.existsBySeekerSeekerIdAndJobJobId(seekerId, jobId)) {
            throw new RuntimeException("Already added to favorites");
        }

        FavoriteJob fav = new FavoriteJob();
        fav.setSeeker(seeker);
        fav.setJob(job);

        return favoriteJobRepo.save(fav);
    }

    @Override
    public List<FavoriteJobDTO> getFavorites(Long seekerId) {

        List<FavoriteJob> favorites =
                favoriteJobRepo.findBySeekerSeekerId(seekerId);

        return favorites.stream().map(fav -> {

            Job job = fav.getJob();

            String companyName = (job.getEmployer() != null)
                    ? job.getEmployer().getCompanyName()
                    : "Unknown Company";

            return new FavoriteJobDTO(
                    fav.getFavId(),
                    job.getJobId(),
                    job.getTitle(),
                    job.getLocation(),
                    job.getSalaryMin(),
                    job.getSalaryMax(),
                    job.getJobType(),
                    job.getStatus(),
                    companyName
            );

        }).toList();
    }

    @Override
    public void removeFavoriteJob(Long favId) {
        favoriteJobRepo.deleteById(favId);
    }

    // ========================= NOTIFICATIONS =========================

    @Override
    public void markNotificationAsRead(Long notificationId) {

        Notification notification = notificationRepo.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        notification.setIsRead(true);
        notificationRepo.save(notification);
    }

    // ========================= JOB SEARCH =========================

    @Override
    public List<JobDTO> searchJobs(String title,
                                   String location,
                                   Integer exp,
                                   String edu,
                                   Double minSal,
                                   Double maxSal,
                                   String type) {

        BigDecimal bMin = (minSal != null)
                ? BigDecimal.valueOf(minSal)
                : BigDecimal.ZERO;

        BigDecimal bMax = (maxSal != null)
                ? BigDecimal.valueOf(maxSal)
                : BigDecimal.valueOf(9999999);

        List<Job> jobs = jobRepo.findAdvanced(
                title != null ? "%" + title + "%" : "%",
                location != null ? "%" + location + "%" : "%",
                exp != null ? exp : 0,
                edu != null ? "%" + edu + "%" : "%",
                bMin,
                bMax,
                type != null ? "%" + type + "%" : "%",
                "OPEN"
        );

        return jobs.stream().map(this::convertToDTO).toList();
    }

    // ========================= PRIVATE DTO CONVERTER =========================

    private JobDTO convertToDTO(Job job) {

        String companyName = (job.getEmployer() != null)
                ? job.getEmployer().getCompanyName()
                : "Unknown Company";

        return new JobDTO(
                job.getJobId(),
                job.getTitle(),
                job.getLocation(),
                job.getSalaryMin(),
                job.getSalaryMax(),
                job.getJobType(),
                job.getStatus(),
                companyName
        );
    }
    // ========================= RECOMMENDED JOBS =========================

    @Override
    public List<Job> getRecommendedJobs(List<String> skills) {

        if (skills == null || skills.isEmpty()) {
            return List.of();
        }

        List<Job> allJobs = jobRepo.findAll();

        return allJobs.stream()
                .filter(job ->
                        skills.stream().anyMatch(skill ->
                                job.getTitle() != null &&
                                        job.getTitle().toLowerCase().contains(skill.toLowerCase())
                        )
                )
                .limit(10)
                .toList();
    }

}