package com.RevHire.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.RevHire.entity.FavoriteJob;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public interface FavoriteJobRepository extends JpaRepository<FavoriteJob, Long> {

    Logger logger = LogManager.getLogger(FavoriteJobRepository.class);

    List<FavoriteJob> findBySeekerSeekerId(Long seekerId);

    boolean existsBySeekerSeekerIdAndJobJobId(Long seekerId, Long jobId);

    long countBySeekerSeekerId(Long seekerId);

}