
package com.RevHire.repository;

import com.RevHire.entity.ResumeProject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import java.util.List;

public interface ResumeProjectRepository extends JpaRepository<ResumeProject, Long> {

    List<ResumeProject> findByResumeResumeId(Long resumeId);

    @Transactional
    @Modifying
    void deleteByResumeResumeId(Long resumeId);
}