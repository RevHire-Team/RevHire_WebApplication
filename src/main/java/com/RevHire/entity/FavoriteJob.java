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