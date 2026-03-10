package com.RevHire.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.RevHire.entity.User;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public interface UserRepository extends JpaRepository<User, Long> {

    Logger logger = LogManager.getLogger(UserRepository.class);

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

}