package com.RevHire.controller;

import com.RevHire.entity.*;
import com.RevHire.repository.ResumeFileRepository;
import com.RevHire.service.ResumeService;
import org.springframework.core.io.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/api/resume")
@CrossOrigin(origins = "*")
public class ResumeController {

    @Autowired
    private final ResumeService resumeService;

    @Autowired
    private ResumeFileRepository resumeFileRepo;

    public ResumeController(ResumeService resumeService,ResumeFileRepository resumeFileRepo) {
        this.resumeService = resumeService;
        this.resumeFileRepo=resumeFileRepo;
    }

    // ================= RESUME CORE =================

    @PostMapping("/create")
    public ResponseEntity<Resume> createResume(@RequestBody Resume resume) {
        return ResponseEntity.ok(resumeService.createResume(resume));
    }

    @PutMapping("/update/{resumeId}")
    public ResponseEntity<Resume> updateResume(@PathVariable Long resumeId,
                                               @RequestBody Resume resume) {
        return ResponseEntity.ok(resumeService.updateResume(resumeId, resume));
    }

    @DeleteMapping("/file/{fileId}")
    public ResponseEntity<String> deleteResumeFile(@PathVariable Long fileId) {
        try {
            resumeService.deleteResumeFile(fileId); // Ensure this calls the FILE service
            return ResponseEntity.ok("Deleted");
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    /**
     * Get complete resume details (Education, Experience, Skills, and Files)
     */
    @GetMapping("/details/{userId}")
    public ResponseEntity<Resume> getResumeDetails(@PathVariable Long userId) {
        Resume resume = resumeService.getResumeByUserId(userId);
        return resume != null ? ResponseEntity.ok(resume) : ResponseEntity.notFound().build();
    }

    // ================= EDUCATION =================

    @PostMapping("/education/add")
    public ResponseEntity<ResumeEducation> addEducation(@RequestBody ResumeEducation education) {
        return ResponseEntity.ok(resumeService.addEducation(education));
    }

    @GetMapping("/education/{resumeId}")
    public ResponseEntity<List<ResumeEducation>> getEducation(@PathVariable Long resumeId) {
        return ResponseEntity.ok(resumeService.getEducationByResume(resumeId));
    }

    @DeleteMapping("/education/delete/{educationId}")
    public ResponseEntity<String> deleteEducation(@PathVariable Long educationId) {
        resumeService.deleteEducation(educationId);
        return ResponseEntity.ok("Education deleted successfully");
    }

    // ================= EXPERIENCE =================

    @PostMapping("/experience/add")
    public ResponseEntity<ResumeExperience> addExperience(@RequestBody ResumeExperience experience) {
        return ResponseEntity.ok(resumeService.addExperience(experience));
    }

    @GetMapping("/experience/{resumeId}")
    public ResponseEntity<List<ResumeExperience>> getExperience(@PathVariable Long resumeId) {
        return ResponseEntity.ok(resumeService.getExperienceByResume(resumeId));
    }

    @DeleteMapping("/experience/delete/{experienceId}")
    public ResponseEntity<String> deleteExperience(@PathVariable Long experienceId) {
        resumeService.deleteExperience(experienceId);
        return ResponseEntity.ok("Experience deleted successfully");
    }

    // ================= SKILLS =================

    @PostMapping("/skill/add")
    public ResponseEntity<ResumeSkill> addSkill(@RequestBody ResumeSkill skill) {
        return ResponseEntity.ok(resumeService.addSkill(skill));
    }

    @GetMapping("/skill/{resumeId}")
    public ResponseEntity<List<ResumeSkill>> getSkills(@PathVariable Long resumeId) {
        return ResponseEntity.ok(resumeService.getSkillsByResume(resumeId));
    }

    @DeleteMapping("/skill/delete/{skillId}")
    public ResponseEntity<String> deleteSkill(@PathVariable Long skillId) {
        resumeService.deleteSkill(skillId);
        return ResponseEntity.ok("Skill deleted successfully");
    }

    // ================= FILE MANAGEMENT =================

    @PostMapping("/upload/{userId}")
    public ResponseEntity<?> uploadFile(@PathVariable Long userId, @RequestParam("file") MultipartFile file) {
        try {
            resumeService.saveResumeFile(userId, file);
            return ResponseEntity.ok().body("{\"message\": \"File uploaded successfully\"}");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    @GetMapping("/download/{fileId}")
    public ResponseEntity<Resource> downloadResumeFile(@PathVariable Long fileId) {

        // 1. Fetch file metadata from DB
        ResumeFile resumeFile = resumeFileRepo
                .findById(fileId)
                .orElseThrow(() -> new RuntimeException("File record not found"));

        try {

            // 2. Get physical file path
            Path path = Paths.get(resumeFile.getFilePath());

            if (!Files.exists(path)) {
                throw new RuntimeException("Physical file missing at: " + path.toAbsolutePath());
            }

            // 3. Load as resource
            Resource resource = new UrlResource(path.toUri());

            // 4. Always return PDF type for browser preview
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(
                            HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + resumeFile.getFileName() + "\""
                    )
                    .body(resource);

        } catch (IOException e) {
            throw new RuntimeException("Could not read file: " + e.getMessage());
        }
    }

    @GetMapping("/view/{fileId}")
    public ResponseEntity<Resource> viewResume(@PathVariable Long fileId) {

        ResumeFile resumeFile = resumeFileRepo
                .findById(fileId)
                .orElseThrow(() -> new RuntimeException("File record not found"));

        try {

            Path path = Paths.get(resumeFile.getFilePath());

            if (!Files.exists(path)) {
                throw new RuntimeException("File missing: " + path.toAbsolutePath());
            }

            Resource resource = new UrlResource(path.toUri());

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "inline; filename=\"" + resumeFile.getFileName() + "\"")
                    .body(resource);

        } catch (IOException e) {
            throw new RuntimeException("Could not read file: " + e.getMessage());
        }
    }


}