package com.RevHire.entity;

import java.time.LocalDateTime;
import java.util.List;
import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "JOBS")
@Getter @Setter
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "job_id")
    private Long jobId;

    @ManyToOne
    @JoinColumn(name = "employer_id", nullable = false)
    private EmployerProfile employer;

    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "experience_required")
    private Integer experienceRequired;

    @Column(name = "education_required")
    private String educationRequired;

    @Column(name = "location")
    private String location;

    @Column(name = "salary_min", precision = 10, scale = 2)
    private BigDecimal salaryMin;

    @Column(name = "salary_max", precision = 10, scale = 2)
    private BigDecimal salaryMax;

    @Column(name = "job_type")
    private String jobType;

    private Integer openings;

    @Column(name = "status")
    private String status;

    @OneToMany(mappedBy = "job")
    private List<JobSkill> skills;

    @OneToMany(mappedBy = "job", cascade = CascadeType.ALL)
    private List<Application> applications;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    @Column(name = "active")
    private Boolean active;

}