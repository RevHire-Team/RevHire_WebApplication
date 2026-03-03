package com.RevHire.repository;

import com.RevHire.entity.Resume;
import com.RevHire.entity.ResumeFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.Modifying;

import java.util.List;
import java.util.Optional;

public interface ResumeFileRepository extends JpaRepository<ResumeFile, Long> {

    List<ResumeFile> findByResume_ResumeId(Long resumeId);

    // To get the file using the Seeker's ID
    Optional<ResumeFile> findByResume_Seeker_SeekerId(Long seekerId);

    // To get the file using the User's ID
    Optional<ResumeFile> findByResume_Seeker_User_UserId(Long userId);

    @Query("SELECT r.resume FROM ResumeFile r WHERE r.resume.seeker.user.userId = :userId")
    Resume getResumeByUserId(@Param("userId") Long userId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    @Query("DELETE FROM ResumeFile rf WHERE rf.resume = :resume")
    void deleteByResume(@Param("resume") Resume resume);

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("DELETE FROM ResumeFile rf WHERE rf.fileId = :fileId")
    void deleteByFileId(@Param("fileId") Long fileId);

}