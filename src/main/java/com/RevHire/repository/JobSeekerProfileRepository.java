package com.RevHire.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.RevHire.entity.JobSeekerProfile;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public interface JobSeekerProfileRepository extends JpaRepository<JobSeekerProfile, Long> {

    Logger logger = LogManager.getLogger(JobSeekerProfileRepository.class);

    Optional<JobSeekerProfile> findByUserUserId(Long userId);

}