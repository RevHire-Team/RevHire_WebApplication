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

    public JobSeekerServiceImpl(JobSeekerProfileRepository profileRepo,
                                ResumeRepository resumeRepo,
                                ResumeFileRepository resumeFileRepo,
                                FavoriteJobRepository favoriteJobRepo,
                                NotificationRepository notificationRepo) {
        this.profileRepo = profileRepo;
        this.resumeRepo = resumeRepo;
        this.resumeFileRepo = resumeFileRepo;
        this.favoriteJobRepo = favoriteJobRepo;
        this.notificationRepo = notificationRepo;
    }

    // ========== PROFILE ==========
    @Override
    public JobSeekerProfile createProfile(JobSeekerProfile profile) {
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
        Resume resume = resumeRepo.findById(resumeId)
                .orElseThrow(() -> new RuntimeException("Resume not found"));

        // Simple file save to local folder (adjust path as needed)
        String uploadDir = "uploads/resumes/";
        File folder = new File(uploadDir);
        if (!folder.exists()) folder.mkdirs();

        String filePath = uploadDir + file.getOriginalFilename();
        try {
            file.transferTo(new File(filePath));
        } catch (IOException e) {
            throw new RuntimeException("Failed to save file", e);
        }

        ResumeFile resumeFile = new ResumeFile();
        resumeFile.setResume(resume);
        resumeFile.setFileName(file.getOriginalFilename());
        resumeFile.setFilePath(filePath);
        resumeFile.setFileSize(file.getSize());
        resumeFile.setFileType(file.getContentType());

        return resumeFileRepo.save(resumeFile);
    }

    // ========== FAVORITE JOBS ==========
    @Override
    public FavoriteJob addFavoriteJob(Long seekerId, Long jobId) {
        FavoriteJob fav = new FavoriteJob();
        JobSeekerProfile seeker = profileRepo.findById(seekerId)
                .orElseThrow(() -> new RuntimeException("Seeker not found"));
        Job job = new Job();
        job.setJobId(jobId); // assuming Job entity exists
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
        notification.setIsRead("YES");
        notificationRepo.save(notification);
    }
}