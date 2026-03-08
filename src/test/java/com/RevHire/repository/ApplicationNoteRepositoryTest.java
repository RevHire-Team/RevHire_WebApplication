package com.RevHire.repository;

import com.RevHire.entity.Application;
import com.RevHire.entity.ApplicationNote;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class ApplicationNoteRepositoryTest {

    @Autowired
    private ApplicationNoteRepository applicationNoteRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Test
    void testSaveAndFind() {
        // Step 1: Create Application object
        Application app = new Application();
        // ✅ Replace the setters with actual fields of Application
        // e.g., app.setApplicantName("John Doe");
        // app.setEmail("john@example.com");
        // app.setAppliedDate(LocalDate.now());

        applicationRepository.save(app);

        // Step 2: Create ApplicationNote object and link to Application
        ApplicationNote note = new ApplicationNote();
        // ✅ Replace with actual setter in your entity
        note.setNoteText("This is a test note");
        note.setApplication(app);

        applicationNoteRepository.save(note);

        // Step 3: Retrieve saved note
        // Replace getId() with the actual primary key getter of ApplicationNote
        // Example: if your primary key field is 'noteId', use note.getNoteId()
        Long noteId = note.getNoteId();

        ApplicationNote savedNote = applicationNoteRepository.findById(noteId).orElse(null);

        // Step 4: Assertions
        assertThat(savedNote).isNotNull();
        assertThat(savedNote.getNoteText()).isEqualTo("This is a test note");
        assertThat(savedNote.getApplication()).isEqualTo(app);
    }
}