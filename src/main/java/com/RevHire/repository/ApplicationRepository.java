package com.RevHire.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.RevHire.entity.Application;

public interface ApplicationRepository extends JpaRepository<Application, Long> {

    List<Application> findBySeekerSeekerId(Long seekerId);

    List<Application> findByJobJobId(Long jobId);

    Optional<Application> findByJobJobIdAndSeekerSeekerId(Long jobId, Long seekerId);

    List<Application> findByJobEmployerEmployerId(Long employerId);

    Long countByJob_Employer_EmployerId(Long employerId);
    Long countByJob_Employer_EmployerIdAndStatus(Long employerId, String status);

}
