package com.RevHire.service;

import com.RevHire.dto.FavoriteJobDTO;
import com.RevHire.dto.JobDTO;
import com.RevHire.entity.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface JobSeekerService {

    // ========== PROFILE ==========
    JobSeekerProfile createProfile(JobSeekerProfile profile, Long userId);

    Optional<JobSeekerProfile> getProfile(Long userId);

    JobSeekerProfile updateProfile(Long profileId, JobSeekerProfile profile);

    // ========== RESUME ==========
    Resume getOrCreateResume(Long userId);

    ResumeFile uploadResumeFile(Long userId, MultipartFile file);

    // ========== FAVORITE JOBS ==========
    FavoriteJob addFavoriteJob(Long seekerId, Long jobId);

    List<FavoriteJobDTO> getFavorites(Long seekerId);

    void removeFavoriteJob(Long favId);

    // ========== NOTIFICATIONS ==========
    void markNotificationAsRead(Long notificationId);

    // ========== JOB SEARCH ==========
    List<JobDTO> searchJobs(String title,
                            String location,
                            Integer exp,
                            String edu,
                            Double minSal,
                            Double maxSal,
                            String type);

    // ========== RECOMMENDED JOBS ==========
    List<Job> getRecommendedJobs(List<String> skills);
}