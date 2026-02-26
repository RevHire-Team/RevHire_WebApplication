public class ResumeEducationDTO {

    private Long educationId;
    private Long resumeId;
    private String degree;
    private String institution;
    private Integer yearOfCompletion;

    public ResumeEducationDTO() {}

    public ResumeEducationDTO(Long educationId, Long resumeId,
                              String degree, String institution,
                              Integer yearOfCompletion) {
        this.educationId = educationId;
        this.resumeId = resumeId;
        this.degree = degree;
        this.institution = institution;
        this.yearOfCompletion = yearOfCompletion;
    }

    // Getters & Setters
    public Long getEducationId() { return educationId; }
    public void setEducationId(Long educationId) { this.educationId = educationId; }

    public Long getResumeId() { return resumeId; }
    public void setResumeId(Long resumeId) { this.resumeId = resumeId; }

    public String getDegree() { return degree; }
    public void setDegree(String degree) { this.degree = degree; }

    public String getInstitution() { return institution; }
    public void setInstitution(String institution) { this.institution = institution; }

    public Integer getYearOfCompletion() { return yearOfCompletion; }
    public void setYearOfCompletion(Integer yearOfCompletion) {
        this.yearOfCompletion = yearOfCompletion;
    }
}