package com.RevHire.repository;

import com.RevHire.entity.Resume;
import com.RevHire.entity.ResumeEducation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.Modifying;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public interface ResumeEducationRepository extends JpaRepository<ResumeEducation, Long> {

    Logger logger = LogManager.getLogger(ResumeEducationRepository.class);

    List<ResumeEducation> findByResume_ResumeId(Long resumeId);

    List<ResumeEducation> findByResumeResumeId(Long resumeId);

    void deleteByResume(Resume resume);

    @Transactional
    @Modifying
    void deleteByResumeResumeId(Long resumeId);

}