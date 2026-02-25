package com.RevHire.service;

import com.RevHire.entity.JobSeekerProfile;
import java.util.Optional;

public interface JobSeekerService {

    JobSeekerProfile createProfile(JobSeekerProfile profile);

    Optional<JobSeekerProfile> getProfileByUserId(Long userId);

    JobSeekerProfile updateProfile(Long profileId, JobSeekerProfile profile);

    void deleteProfile(Long profileId);
}