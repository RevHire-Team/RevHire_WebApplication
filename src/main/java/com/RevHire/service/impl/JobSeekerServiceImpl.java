package com.RevHire.service.impl;

import com.RevHire.entity.*;
import com.RevHire.repository.*;
import com.RevHire.service.JobSeekerService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
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


    public JobSeekerServiceImpl(JobSeekerProfileRepository profileRepo,
                                ResumeRepository resumeRepo,
                                ResumeFileRepository resumeFileRepo,
                                FavoriteJobRepository favoriteJobRepo,
                                NotificationRepository notificationRepo,
                                UserRepository userRepo,
                                JobRepository jobRepo) {
        this.profileRepo = profileRepo;
        this.resumeRepo = resumeRepo;
        this.resumeFileRepo = resumeFileRepo;
        this.favoriteJobRepo = favoriteJobRepo;
        this.notificationRepo = notificationRepo;
        this.userRepo=userRepo;
        this.jobRepo=jobRepo;
    }

    // ========== PROFILE ==========
    @Override
    public JobSeekerProfile createProfile(JobSeekerProfile profile) {
        System.out.println(profile.getUser());
        System.out.println(profile.getUser().getUserId());
        Long userId = profile.getUser().getUserId();
        System.out.println("User ID received: " + userId);

        Optional<User> userOptional = userRepo.findById(userId);
        System.out.println("User present? " + userOptional.isPresent());

        User user = userOptional
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        profile.setUser(user);

        return profileRepo.save(profile);
    }

    @Override
    public Optional<JobSeekerProfile> getProfile(Long userId) {
        return profileRepo.findByUserUserId(userId);
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

    // ========== RESUME FILE UPLOAD ==========
    @Override
    public ResumeFile uploadResumeFile(Long resumeId, MultipartFile file) {

        System.out.println("Incoming resumeId: " + resumeId);

        List<Resume> allResumes = resumeRepo.findAll();
        System.out.println("Total resumes in DB: " + allResumes.size());

        Resume resume = resumeRepo.findById(resumeId)
                .orElseThrow(() -> new RuntimeException("Resume not found"));

        try {
            // Get project root directory
            String projectPath = System.getProperty("user.dir");

            String uploadDir = projectPath + File.separator + "uploads" + File.separator + "resumes";

            File folder = new File(uploadDir);
            if (!folder.exists()) {
                folder.mkdirs();
            }

            // Prevent overwrite
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();

            File destination = new File(uploadDir + File.separator + fileName);

            file.transferTo(destination);

            ResumeFile resumeFile = new ResumeFile();
            resumeFile.setResume(resume);
            resumeFile.setFileName(fileName);
            resumeFile.setFilePath(destination.getAbsolutePath());
            resumeFile.setFileSize(file.getSize());
            String extension = file.getOriginalFilename()
                    .substring(file.getOriginalFilename().lastIndexOf(".") + 1)
                    .toUpperCase();

            resumeFile.setFileType(extension);

            return resumeFileRepo.save(resumeFile);

        } catch (IOException e) {
            throw new RuntimeException("Failed to save file", e);
        }
    }

    // ========== FAVORITE JOBS ==========
    @Override
    public FavoriteJob addFavoriteJob(Long seekerId, Long jobId) {
        System.out.println("Total seekers in DB: " + profileRepo.count());
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
        public List<FavoriteJob> getFavorites(Long seekerId) {
            return favoriteJobRepo.findBySeekerSeekerId(seekerId);
        }

    @Override
    public void removeFavoriteJob(Long favId) {
        favoriteJobRepo.deleteById(favId);
    }

    // ========== NOTIFICATIONS ==========
    @Override
    public void markNotificationAsRead(Long notificationId) {
        Notification notification = notificationRepo.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        notification.setIsRead("Y");
        notificationRepo.save(notification);
    }
}