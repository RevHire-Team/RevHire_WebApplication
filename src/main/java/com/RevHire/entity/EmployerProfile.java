package com.revhire.entity;

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
