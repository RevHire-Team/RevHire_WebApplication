@Entity
@Table(name = "applications")
@Getter @Setter
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

    @Lob
    private String coverLetter;

    private String status;

    @Column(name = "withdraw_reason")
    private String withdrawReason;

    @OneToMany(mappedBy = "application")
    private List<ApplicationNote> notes;
}