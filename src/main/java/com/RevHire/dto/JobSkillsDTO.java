public class JobSkillsDTO {

    private Long jobSkillId;
    private Long jobId;
    private String skillName;

    public JobSkillsDTO() {}

    public JobSkillsDTO(Long jobSkillId, Long jobId, String skillName) {
        this.jobSkillId = jobSkillId;
        this.jobId = jobId;
        this.skillName = skillName;
    }

    public Long getJobSkillId() { return jobSkillId; }
    public void setJobSkillId(Long jobSkillId) { this.jobSkillId = jobSkillId; }

    public Long getJobId() { return jobId; }
    public void setJobId(Long jobId) { this.jobId = jobId; }

    public String getSkillName() { return skillName; }
    public void setSkillName(String skillName) { this.skillName = skillName; }
}