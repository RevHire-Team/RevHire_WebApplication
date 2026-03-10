package com.RevHire.repository;

import com.RevHire.entity.ResumeSkill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.Modifying;

import java.util.List;

public interface ResumeSkillRepository extends JpaRepository<ResumeSkill, Long> {

    List<ResumeSkill> findByResumeResumeId(Long resumeId);

    @Transactional
    @Modifying
    void deleteByResumeResumeId(Long resumeId);
}