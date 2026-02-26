package com.RevHire.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.RevHire.entity.ResumeFile;

public interface ResumeFileRepository extends JpaRepository<ResumeFile, Long> {

}