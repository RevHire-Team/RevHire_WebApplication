package com.RevHire.entity;

import java.time.LocalDateTime;
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
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "applications")
@Getter
@Setter
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

    @Column(name = "cover_letter")
    private String coverLetter;

    @Column(name = "status")
    private String status;

    @Column(name = "applied_date", insertable = false, updatable = false)
    private LocalDateTime appliedDate;

    @Column(name = "withdraw_reason")
    private String withdrawReason;

    @OneToMany(mappedBy = "application")
    private List<ApplicationNote> notes;
    
    
}