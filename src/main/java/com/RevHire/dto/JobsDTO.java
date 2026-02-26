import java.time.LocalDate;
import java.time.LocalDateTime;

public class JobsDTO {

    private Long jobId;
    private Long employerId;
    private String title;
    private String description;
    private Integer experienceRequired;
    private String educationRequired;
    private String location;
    private Double salaryMin;
    private Double salaryMax;
    private String jobType;
    private LocalDate deadline;
    private Integer openings;
    private String status;
    private LocalDateTime createdAt;

    public JobsDTO() {}

    public JobsDTO(Long jobId, Long employerId, String title, String description,
                   Integer experienceRequired, String educationRequired,
                   String location, Double salaryMin, Double salaryMax,
                   String jobType, LocalDate deadline, Integer openings,
                   String status, LocalDateTime createdAt) {
        this.jobId = jobId;
        this.employerId = employerId;
        this.title = title;
        this.description = description;
        this.experienceRequired = experienceRequired;
        this.educationRequired = educationRequired;
        this.location = location;
        this.salaryMin = salaryMin;
        this.salaryMax = salaryMax;
        this.jobType = jobType;
        this.deadline = deadline;
        this.openings = openings;
        this.status = status;
        this.createdAt = createdAt;
    }

    // Getters & Setters
    public Long getJobId() { return jobId; }
    public void setJobId(Long jobId) { this.jobId = jobId; }

    public Long getEmployerId() { return employerId; }
    public void setEmployerId(Long employerId) { this.employerId = employerId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getExperienceRequired() { return experienceRequired; }
    public void setExperienceRequired(Integer experienceRequired) {
        this.experienceRequired = experienceRequired;
    }

    public String getEducationRequired() { return educationRequired; }
    public void setEducationRequired(String educationRequired) {
        this.educationRequired = educationRequired;
    }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public Double getSalaryMin() { return salaryMin; }
    public void setSalaryMin(Double salaryMin) { this.salaryMin = salaryMin; }

    public Double getSalaryMax() { return salaryMax; }
    public void setSalaryMax(Double salaryMax) { this.salaryMax = salaryMax; }

    public String getJobType() { return jobType; }
    public void setJobType(String jobType) { this.jobType = jobType; }

    public LocalDate getDeadline() { return deadline; }
    public void setDeadline(LocalDate deadline) { this.deadline = deadline; }

    public Integer getOpenings() { return openings; }
    public void setOpenings(Integer openings) { this.openings = openings; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}