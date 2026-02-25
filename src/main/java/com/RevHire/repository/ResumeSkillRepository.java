package com.RevHire.repository;

import com.RevHire.entity.ResumeExperience;
import org.springframework.data.jpa.repository.JpaRepository;

import com.RevHire.entity.ResumeSkill;

import java.util.List;

public interface ResumeSkillRepository extends JpaRepository<ResumeSkill, Long> {
    List<ResumeSkill> findByResume_ResumeId(Long resumeId);
}
