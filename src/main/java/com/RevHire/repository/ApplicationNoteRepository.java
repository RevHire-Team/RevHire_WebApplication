package com.RevHire.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.RevHire.entity.ApplicationNote;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;

@Repository
public interface ApplicationNoteRepository extends JpaRepository<ApplicationNote, Long> {

    Logger logger = LogManager.getLogger(ApplicationNoteRepository.class);

    List<ApplicationNote> findByApplicationApplicationId(Long applicationId);

}