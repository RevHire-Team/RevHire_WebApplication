package com.RevHire.entity;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "applications")
public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "application_id")
    private Long applicationId;

    @ManyToOne
    @JoinColumn(name = "job_id", nullable = false)
    private Job job;

    @ManyToOne
    @JoinColumn(name = "seeker_id", nullable = false)
    private JobSeekerProfile seeker;

    @ManyToOne
    @JoinColumn(name = "resume_id")
    private Resume resume;

    @Lob
    private String coverLetter;

    private String status;

    @Column(name = "withdraw_reason")
    private String withdrawReason;

    @OneToMany(mappedBy = "application")
    private List<ApplicationNote> notes;
    
    
}