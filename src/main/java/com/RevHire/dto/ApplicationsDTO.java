import java.time.LocalDateTime;

public class ApplicationsDTO {

    private Long applicationId;
    private Long jobId;
    private Long seekerId;
    private Long resumeId;
    private String coverLetter;
    private String status;
    private LocalDateTime appliedDate;
    private String withdrawReason;

    public ApplicationsDTO() {}

    public ApplicationsDTO(Long applicationId, Long jobId, Long seekerId,
                           Long resumeId, String coverLetter,
                           String status, LocalDateTime appliedDate,
                           String withdrawReason) {
        this.applicationId = applicationId;
        this.jobId = jobId;
        this.seekerId = seekerId;
        this.resumeId = resumeId;
        this.coverLetter = coverLetter;
        this.status = status;
        this.appliedDate = appliedDate;
        this.withdrawReason = withdrawReason;
    }

    // Getters & Setters
    public Long getApplicationId() { return applicationId; }
    public void setApplicationId(Long applicationId) { this.applicationId = applicationId; }

    public Long getJobId() { return jobId; }
    public void setJobId(Long jobId) { this.jobId = jobId; }

    public Long getSeekerId() { return seekerId; }
    public void setSeekerId(Long seekerId) { this.seekerId = seekerId; }

    public Long getResumeId() { return resumeId; }
    public void setResumeId(Long resumeId) { this.resumeId = resumeId; }

    public String getCoverLetter() { return coverLetter; }
    public void setCoverLetter(String coverLetter) { this.coverLetter = coverLetter; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getAppliedDate() { return appliedDate; }
    public void setAppliedDate(LocalDateTime appliedDate) { this.appliedDate = appliedDate; }

    public String getWithdrawReason() { return withdrawReason; }
    public void setWithdrawReason(String withdrawReason) {
        this.withdrawReason = withdrawReason;
    }
}