package com.RevHire.repository;

import java.time.LocalDateTime;
import java.util.List;

import com.RevHire.dto.JobDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.RevHire.entity.Job;
import org.springframework.data.repository.query.Param;

public interface JobRepository extends JpaRepository<Job, Long> {

    List<Job> findByStatus(String status);

    List<Job> findByLocationContainingIgnoreCase(String location);

    List<Job> findByTitleContainingIgnoreCase(String title);

    List<Job> findBySalaryMinGreaterThanEqual(Long salary);

    List<Job> findByEmployerEmployerId(Long employerId);

    Long countByEmployerEmployerId(Long employerId);

    Long countByEmployerEmployerIdAndStatus(Long employerId, String status);

    @Query("""
SELECT new com.RevHire.dto.JobDTO(
    j.jobId,
    j.title,
    j.location,
    j.salaryMin,
    j.salaryMax,
    j.jobType,
    j.status,
    e.companyName
)
FROM Job j
JOIN j.employer e
WHERE (:title IS NULL OR LOWER(j.title) LIKE LOWER(CONCAT('%', TRIM(:title), '%')))
AND (:location IS NULL OR LOWER(j.location) LIKE LOWER(CONCAT('%', TRIM(:location), '%')))
AND (:experience IS NULL OR j.experienceRequired <= :experience)
AND (:companyName IS NULL OR LOWER(e.companyName) LIKE LOWER(CONCAT('%', TRIM(:companyName), '%')))
AND (:minSalary IS NULL OR j.salaryMin >= :minSalary)
AND (:maxSalary IS NULL OR j.salaryMax <= :maxSalary)
AND (:jobType IS NULL OR j.jobType = :jobType)
AND j.status = 'OPEN'
""")
    List<JobDTO> advancedSearch(
            @Param("title") String title,
            @Param("location") String location,
            @Param("experience") Integer experience,
            @Param("companyName") String companyName,
            @Param("minSalary") Double minSalary,
            @Param("maxSalary") Double maxSalary,
            @Param("jobType") String jobType
    );

}