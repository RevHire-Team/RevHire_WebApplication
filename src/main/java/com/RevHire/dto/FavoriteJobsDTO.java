import java.time.LocalDateTime;

public class FavoriteJobsDTO {

    private Long favId;
    private Long seekerId;
    private Long jobId;
    private LocalDateTime createdAt;

    public FavoriteJobsDTO() {}

    public FavoriteJobsDTO(Long favId, Long seekerId, Long jobId,
                           LocalDateTime createdAt) {
        this.favId = favId;
        this.seekerId = seekerId;
        this.jobId = jobId;
        this.createdAt = createdAt;
    }

    // Getters & Setters
    public Long getFavId() { return favId; }
    public void setFavId(Long favId) { this.favId = favId; }

    public Long getSeekerId() { return seekerId; }
    public void setSeekerId(Long seekerId) { this.seekerId = seekerId; }

    public Long getJobId() { return jobId; }
    public void setJobId(Long jobId) { this.jobId = jobId; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}