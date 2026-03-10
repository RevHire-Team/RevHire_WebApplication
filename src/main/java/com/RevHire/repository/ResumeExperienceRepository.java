package com.RevHire.repository;

import com.RevHire.entity.Resume;
import com.RevHire.entity.ResumeEducation;
import org.springframework.data.jpa.repository.JpaRepository;
import com.RevHire.entity.ResumeExperience;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import java.util.List;

public interface ResumeExperienceRepository extends JpaRepository<ResumeExperience, Long> {

    List<ResumeExperience> findByResume_ResumeId(Long resumeId);

    List<ResumeExperience> findByResumeResumeId(Long resumeId);

    void deleteByResume(Resume resume);

    @Transactional
    @Modifying
    void deleteByResumeResumeId(Long resumeId);
}

