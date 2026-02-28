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

    private String title;
    private String description;
    private Integer experienceRequired;
    private String educationRequired;
    private String location;

    @Column(precision = 10, scale = 2)
    private BigDecimal salaryMin;

    @Column(precision = 10, scale = 2)
    private BigDecimal salaryMax;

    private String jobType;
    private Integer openings;
    private String status;

    @OneToMany(mappedBy = "job",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<JobSkill> skills;

    @OneToMany(mappedBy = "job",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<Application> applications;

    @OneToMany(mappedBy = "job",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<FavoriteJob> favoriteJobs;

    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    private Boolean active;
}