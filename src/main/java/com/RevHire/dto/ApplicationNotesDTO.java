import java.time.LocalDateTime;


public class ApplicationNotesDTO {

    private Long noteId;
    private Long applicationId;
    private Long employerId;
    private String noteText;
    private LocalDateTime createdAt;

    public ApplicationNotesDTO() {}

    public ApplicationNotesDTO(Long noteId, Long applicationId,
                               Long employerId, String noteText,
                               LocalDateTime createdAt) {
        this.noteId = noteId;
        this.applicationId = applicationId;
        this.employerId = employerId;
        this.noteText = noteText;
        this.createdAt = createdAt;
    }

    // Getters & Setters
    public Long getNoteId() { return noteId; }
    public void setNoteId(Long noteId) { this.noteId = noteId; }

    public Long getApplicationId() { return applicationId; }
    public void setApplicationId(Long applicationId) { this.applicationId = applicationId; }

    public Long getEmployerId() { return employerId; }
    public void setEmployerId(Long employerId) { this.employerId = employerId; }

    public String getNoteText() { return noteText; }
    public void setNoteText(String noteText) { this.noteText = noteText; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    
}