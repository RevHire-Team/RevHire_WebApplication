package com.RevHire.repository;

import com.RevHire.entity.Resume;
import com.RevHire.entity.ResumeEducation;
import com.RevHire.entity.ResumeExperience;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.Modifying;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public interface ResumeExperienceRepository extends JpaRepository<ResumeExperience, Long> {

    Logger logger = LogManager.getLogger(ResumeExperienceRepository.class);

    List<ResumeExperience> findByResume_ResumeId(Long resumeId);

    List<ResumeExperience> findByResumeResumeId(Long resumeId);

    void deleteByResume(Resume resume);

    @Transactional
    @Modifying
    void deleteByResumeResumeId(Long resumeId);
}