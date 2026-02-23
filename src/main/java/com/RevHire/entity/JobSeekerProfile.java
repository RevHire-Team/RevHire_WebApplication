package com.revhire.entity;

@Entity
@Table(name = "job_seeker_profiles")
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
    private Integer profileCompletion;
}