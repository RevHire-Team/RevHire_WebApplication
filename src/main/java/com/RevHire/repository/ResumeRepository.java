package com.RevHire.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.RevHire.entity.Resume;

public interface ResumeRepository extends JpaRepository<Resume, Long> {

    Optional<Resume> findBySeekerSeekerId(Long seekerId);

    // This tells JPA: Go to 'seeker' property, then find 'seekerId' inside it
    Optional<Resume> findBySeeker_SeekerId(Long seekerId);

    Optional<Resume> findBySeeker_User_UserId(Long userId);
}