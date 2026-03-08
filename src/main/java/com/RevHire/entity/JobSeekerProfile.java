package com.RevHire.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

@Entity
@Table(name = "JOB_SEEKER_PROFILES")
@Getter
@Setter
public class JobSeekerProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long seekerId;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String fullName;
    private String phone;
    private String location;
    private String currentEmploymentStatus;
    private Integer totalExperience;

    @Column(name = "profile_completion")
    private Integer profileCompletion;

    // Inside JobSeekerProfile.java
    @OneToMany(mappedBy = "seeker", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Resume> resumes;

    @OneToMany(mappedBy = "seeker", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Application> applications;

    @OneToMany(mappedBy = "seeker", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FavoriteJob> favoriteJobs;

}