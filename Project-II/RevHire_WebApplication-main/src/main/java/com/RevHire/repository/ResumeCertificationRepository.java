package com.RevHire.repository;

import com.RevHire.entity.ResumeCertification;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.Modifying;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Repository
public interface ResumeCertificationRepository extends JpaRepository<ResumeCertification, Long> {

    Logger logger = LogManager.getLogger(ResumeCertificationRepository.class);

    List<ResumeCertification> findByResumeResumeId(Long resumeId);

    @Transactional
    @Modifying
    void deleteByResumeResumeId(Long resumeId);

}