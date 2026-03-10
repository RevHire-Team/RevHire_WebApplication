package com.RevHire.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.RevHire.entity.JobSkill;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;

@Repository
public interface JobSkillRepository extends JpaRepository<JobSkill, Long> {

    Logger logger = LogManager.getLogger(JobSkillRepository.class);

    List<JobSkill> findByJobJobId(Long jobId);

}