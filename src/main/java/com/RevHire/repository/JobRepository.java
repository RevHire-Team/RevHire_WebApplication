package com.RevHire.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.RevHire.entity.Job;

public interface JobRepository extends JpaRepository<Job, Long> {

    List<Job> findByStatus(String status);

    List<Job> findByLocationContainingIgnoreCase(String location);

    List<Job> findByTitleContainingIgnoreCase(String title);

    List<Job> findBySalaryMinGreaterThanEqual(Long salary);

    List<Job> findByEmployerEmployerId(Long employerId);

    @Query("""
        SELECT j FROM Job j 
        WHERE (:location IS NULL OR LOWER(j.location) LIKE LOWER(CONCAT('%',:location,'%')))
        AND (:title IS NULL OR LOWER(j.title) LIKE LOWER(CONCAT('%',:title,'%')))
        AND (:jobType IS NULL OR j.jobType = :jobType)
        AND j.status = 'OPEN'
    """)
    List<Job> advancedSearch(String location, String title, String jobType);
}