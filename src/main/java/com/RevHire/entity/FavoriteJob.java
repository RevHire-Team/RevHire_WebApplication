package com.RevHire.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "favorite_jobs")
@Getter @Setter
public class FavoriteJob {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "fav_id")
    private Long favId;

    @ManyToOne
    @JoinColumn(name = "seeker_id", nullable = false)
    private JobSeekerProfile seeker;

    @ManyToOne
    @JoinColumn(name = "job_id", nullable = false)
    private Job job;
}