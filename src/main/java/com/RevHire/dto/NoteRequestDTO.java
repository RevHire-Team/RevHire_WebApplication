package com.RevHire.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NoteRequestDTO {
    private Long employerId;
    private String noteText;
}