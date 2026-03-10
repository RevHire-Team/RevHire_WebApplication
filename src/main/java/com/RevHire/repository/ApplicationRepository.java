package com.RevHire.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.RevHire.entity.Application;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public interface ApplicationRepository extends JpaRepository<Application, Long> {

    Logger logger = LogManager.getLogger(ApplicationRepository.class);

    List<Application> findBySeekerSeekerId(Long seekerId);

    List<Application> findByJobJobId(Long jobId);

    Optional<Application> findByJobJobIdAndSeekerSeekerId(Long jobId, Long seekerId);

    List<Application> findByJobEmployerEmployerId(Long employerId);

    Long countByJob_Employer_EmployerId(Long employerId);

    Long countByJob_Employer_EmployerIdAndStatus(Long employerId, String status);
}