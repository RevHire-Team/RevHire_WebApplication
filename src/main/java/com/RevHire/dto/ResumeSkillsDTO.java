public class ResumeSkillsDTO {

    private Long skillId;
    private Long resumeId;
    private String skillName;

    public ResumeSkillsDTO() {}

    public ResumeSkillsDTO(Long skillId, Long resumeId, String skillName) {
        this.skillId = skillId;
        this.resumeId = resumeId;
        this.skillName = skillName;
    }

    public Long getSkillId() { return skillId; }
    public void setSkillId(Long skillId) { this.skillId = skillId; }

    public Long getResumeId() { return resumeId; }
    public void setResumeId(Long resumeId) { this.resumeId = resumeId; }

    public String getSkillName() { return skillName; }
    public void setSkillName(String skillName) { this.skillName = skillName; }
}