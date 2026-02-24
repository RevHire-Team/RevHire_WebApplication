package com.RevHire.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "employer_profiles")
@Getter
@Setter
public class EmployerProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long employerId;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String companyName;
    private String industry;
    private Integer companySize;

    @Lob
    private String description;

    private String website;
    private String location;
}
