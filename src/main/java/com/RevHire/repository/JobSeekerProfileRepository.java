package com.RevHire.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.RevHire.entity.JobSeekerProfile;

public interface JobSeekerProfileRepository extends JpaRepository<JobSeekerProfile, Long> {

    Optional<JobSeekerProfile> findByUserUserId(Long userId);

}