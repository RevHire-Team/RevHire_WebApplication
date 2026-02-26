public class EmployerProfileDTO {

    private Long employerId;
    private Long userId;
    private String companyName;
    private String industry;
    private Integer companySize;
    private String description;
    private String website;
    private String location;

    public EmployerProfileDTO() {}

    public EmployerProfileDTO(Long employerId, Long userId, String companyName,
                              String industry, Integer companySize,
                              String description, String website,
                              String location) {
        this.employerId = employerId;
        this.userId = userId;
        this.companyName = companyName;
        this.industry = industry;
        this.companySize = companySize;
        this.description = description;
        this.website = website;
        this.location = location;
    }

    // Getters & Setters
    public Long getEmployerId() { return employerId; }
    public void setEmployerId(Long employerId) { this.employerId = employerId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public String getIndustry() { return industry; }
    public void setIndustry(String industry) { this.industry = industry; }

    public Integer getCompanySize() { return companySize; }
    public void setCompanySize(Integer companySize) { this.companySize = companySize; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getWebsite() { return website; }
    public void setWebsite(String website) { this.website = website; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
}