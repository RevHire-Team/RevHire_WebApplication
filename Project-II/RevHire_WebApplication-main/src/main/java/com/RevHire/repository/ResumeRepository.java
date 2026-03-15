package com.RevHire.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.RevHire.entity.Resume;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;

@Repository
public interface ResumeRepository extends JpaRepository<Resume, Long> {

    Logger logger = LogManager.getLogger(ResumeRepository.class);

    Optional<Resume> findBySeekerSeekerId(Long seekerId);

    // This tells JPA: Go to 'seeker' property, then find 'seekerId' inside it
    Optional<Resume> findBySeeker_SeekerId(Long seekerId);

    Optional<Resume> findBySeeker_User_UserId(Long userId);

    // Always return latest resume
    Optional<Resume> findTopBySeekerSeekerIdOrderByResumeIdDesc(Long seekerId);

}