@Entity
@Table(name = "resumes")
@Getter @Setter
public class Resume {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "resume_id")
    private Long resumeId;

    @ManyToOne
    @JoinColumn(name = "seeker_id", nullable = false)
    private JobSeekerProfile seeker;

    private String objective;

    @OneToMany(mappedBy = "resume")
    private List<ResumeEducation> educations;

    @OneToMany(mappedBy = "resume")
    private List<ResumeExperience> experiences;

    @OneToMany(mappedBy = "resume")
    private List<ResumeSkill> skills;

    @OneToMany(mappedBy = "resume")
    private List<ResumeFile> files;
}