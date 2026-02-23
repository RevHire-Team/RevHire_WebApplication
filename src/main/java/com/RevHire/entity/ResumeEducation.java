@Entity
@Table(name = "resume_education")
@Getter @Setter
public class ResumeEducation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "education_id")
    private Long educationId;

    @ManyToOne
    @JoinColumn(name = "resume_id", nullable = false)
    private Resume resume;

    private String degree;
    private String institution;

    @Column(name = "year_of_completion")
    private Integer yearOfCompletion;
}