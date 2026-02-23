@Entity
@Table(name = "resume_experience")
@Getter @Setter
public class ResumeExperience {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "experience_id")
    private Long experienceId;

    @ManyToOne
    @JoinColumn(name = "resume_id", nullable = false)
    private Resume resume;

    @Column(name = "company_name")
    private String companyName;

    private String role;
    private Integer years;

    @Lob
    private String description;
}