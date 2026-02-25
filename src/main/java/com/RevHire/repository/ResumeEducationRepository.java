package com.RevHire.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.RevHire.entity.ResumeEducation;

import java.util.List;

public interface ResumeEducationRepository extends JpaRepository<ResumeEducation, Long> {

    List<ResumeEducation> findByResume_ResumeId(Long resumeId);
}