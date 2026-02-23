@Entity
@Table(name = "jobs")
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

    @Lob
    private String description;

    @Column(name = "experience_required")
    private Integer experienceRequired;

    @Column(name = "education_required")
    private String educationRequired;

    private String location;

    @Column(name = "salary_min")
    private Double salaryMin;

    @Column(name = "salary_max")
    private Double salaryMax;

    @Column(name = "job_type")
    private String jobType;

    private Integer openings;
    private String status;

    @OneToMany(mappedBy = "job")
    private List<JobSkill> skills;

    @OneToMany(mappedBy = "job")
    private List<Application> applications;
}