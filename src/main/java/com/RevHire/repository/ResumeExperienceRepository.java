package com.RevHire.repository;

import com.RevHire.entity.ResumeEducation;
import org.springframework.data.jpa.repository.JpaRepository;

import com.RevHire.entity.ResumeExperience;

import java.util.List;

public interface ResumeExperienceRepository extends JpaRepository<ResumeExperience, Long> {

    List<ResumeExperience> findByResume_ResumeId(Long resumeId);
}
