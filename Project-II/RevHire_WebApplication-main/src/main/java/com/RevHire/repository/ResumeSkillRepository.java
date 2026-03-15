package com.RevHire.repository;

import com.RevHire.entity.Resume;
import com.RevHire.entity.ResumeSkill;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.Modifying;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Repository
public interface ResumeSkillRepository extends JpaRepository<ResumeSkill, Long> {

    Logger logger = LogManager.getLogger(ResumeSkillRepository.class);

    List<ResumeSkill> findByResume_ResumeId(Long resumeId);

    void deleteByResume(Resume resume);

    List<ResumeSkill> findByResume(Resume resume);

    List<ResumeSkill> findByResumeResumeId(Long resumeId);

    @Transactional
    @Modifying
    void deleteByResumeResumeId(Long resumeId);
}