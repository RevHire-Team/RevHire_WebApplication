public class JobSeekerProfileDTO {

    private Long seekerId;
    private Long userId;
    private String fullName;
    private String phone;
    private String location;
    private String currentEmploymentStatus;
    private Integer totalExperience;
    private Integer profileCompletion;

    public JobSeekerProfileDTO() {}

    public JobSeekerProfileDTO(Long seekerId, Long userId, String fullName,
                               String phone, String location,
                               String currentEmploymentStatus,
                               Integer totalExperience,
                               Integer profileCompletion) {
        this.seekerId = seekerId;
        this.userId = userId;
        this.fullName = fullName;
        this.phone = phone;
        this.location = location;
        this.currentEmploymentStatus = currentEmploymentStatus;
        this.totalExperience = totalExperience;
        this.profileCompletion = profileCompletion;
    }

    // Getters & Setters
    public Long getSeekerId() { return seekerId; }
    public void setSeekerId(Long seekerId) { this.seekerId = seekerId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getCurrentEmploymentStatus() { return currentEmploymentStatus; }
    public void setCurrentEmploymentStatus(String currentEmploymentStatus) {
        this.currentEmploymentStatus = currentEmploymentStatus;
    }

    public Integer getTotalExperience() { return totalExperience; }
    public void setTotalExperience(Integer totalExperience) { this.totalExperience = totalExperience; }

    public Integer getProfileCompletion() { return profileCompletion; }
    public void setProfileCompletion(Integer profileCompletion) { this.profileCompletion = profileCompletion; }
}