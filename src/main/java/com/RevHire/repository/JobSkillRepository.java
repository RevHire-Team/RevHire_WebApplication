package com.RevHire.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.RevHire.entity.JobSkill;

public interface JobSkillRepository extends JpaRepository<JobSkill, Long> {

    List<JobSkill> findByJobJobId(Long jobId);

}