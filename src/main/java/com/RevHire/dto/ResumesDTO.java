import java.time.LocalDateTime;

public class ResumesDTO {

    private Long resumeId;
    private Long seekerId;
    private String objective;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ResumesDTO() {}

    public ResumesDTO(Long resumeId, Long seekerId, String objective,
                      LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.resumeId = resumeId;
        this.seekerId = seekerId;
        this.objective = objective;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters & Setters
    public Long getResumeId() { return resumeId; }
    public void setResumeId(Long resumeId) { this.resumeId = resumeId; }

    public Long getSeekerId() { return seekerId; }
    public void setSeekerId(Long seekerId) { this.seekerId = seekerId; }

    public String getObjective() { return objective; }
    public void setObjective(String objective) { this.objective = objective; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}