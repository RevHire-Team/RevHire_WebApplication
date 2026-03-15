package com.RevHire.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.RevHire.entity.EmployerProfile;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployerProfileRepository extends JpaRepository<EmployerProfile, Long> {

    Logger logger = LogManager.getLogger(EmployerProfileRepository.class);

    Optional<EmployerProfile> findByUserUserId(Long userId);

}