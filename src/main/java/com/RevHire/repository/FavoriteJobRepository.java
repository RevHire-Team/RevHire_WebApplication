package com.RevHire.repository;

import java.util.List;
import java.util.Optional; // Added for safe finding

import org.springframework.data.jpa.repository.JpaRepository;

import com.RevHire.entity.FavoriteJob;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;

@Repository
public interface FavoriteJobRepository extends JpaRepository<FavoriteJob, Long> {

    Logger logger = LogManager.getLogger(FavoriteJobRepository.class);

    List<FavoriteJob> findBySeekerSeekerId(Long seekerId);

    boolean existsBySeekerSeekerIdAndJobJobId(Long seekerId, Long jobId);

    long countBySeekerSeekerId(Long seekerId);

    // ADDED: This allows the Controller to find the specific favorite record
    // to delete it when the "Unsave" button is clicked.
    Optional<FavoriteJob> findBySeekerSeekerIdAndJobJobId(Long seekerId, Long jobId);

}