package com.RevHire.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NoteRequestDTOTest {

    @Test
    void testSettersAndGetters() {

        NoteRequestDTO dto = new NoteRequestDTO();

        dto.setEmployerId(101L);
        dto.setNoteText("Candidate has good Java skills");

        assertEquals(101L, dto.getEmployerId());
        assertEquals("Candidate has good Java skills", dto.getNoteText());
    }

    @Test
    void testObjectCreation() {

        NoteRequestDTO dto = new NoteRequestDTO();

        assertNotNull(dto);
        assertNull(dto.getEmployerId());
        assertNull(dto.getNoteText());
    }
}