package com.RevHire.controller;

import com.RevHire.dto.EmployerProfileDTO;
import com.RevHire.service.EmployerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/employer")
@RequiredArgsConstructor
public class EmployerController {

    private final EmployerService employerService;

    @PostMapping("/profile/{userId}")
    public ResponseEntity<?> createOrUpdateProfile(
            @PathVariable Long userId,
            @RequestBody EmployerProfileDTO dto) {

        return ResponseEntity.ok(
                employerService.createOrUpdateProfile(userId, dto));
    }

    @GetMapping("/profile/{userId}")
    public ResponseEntity<?> getProfile(@PathVariable Long userId) {

        return ResponseEntity.ok(
                employerService.getProfile(userId));
    }

    @GetMapping("/dashboard/{employerId}")
    public ResponseEntity<?> dashboard(@PathVariable Long employerId) {
        return ResponseEntity.ok(
                employerService.getDashboard(employerId));
    }
}