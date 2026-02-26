public class ResumeExperienceDTO {

    private Long experienceId;
    private Long resumeId;
    private String companyName;
    private String role;
    private Integer years;
    private String description;

    public ResumeExperienceDTO() {}

    public ResumeExperienceDTO(Long experienceId, Long resumeId,
                               String companyName, String role,
                               Integer years, String description) {
        this.experienceId = experienceId;
        this.resumeId = resumeId;
        this.companyName = companyName;
        this.role = role;
        this.years = years;
        this.description = description;
    }

    // Getters & Setters
    public Long getExperienceId() { return experienceId; }
    public void setExperienceId(Long experienceId) { this.experienceId = experienceId; }

    public Long getResumeId() { return resumeId; }
    public void setResumeId(Long resumeId) { this.resumeId = resumeId; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public Integer getYears() { return years; }
    public void setYears(Integer years) { this.years = years; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}