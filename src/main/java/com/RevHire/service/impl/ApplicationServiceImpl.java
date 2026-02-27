package com.RevHire.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import com.RevHire.dto.ApplicationResponseDTO;
import com.RevHire.dto.EmployerApplicationDTO;
import com.RevHire.entity.*;
import com.RevHire.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.RevHire.service.ApplicationService;

@Service
public class ApplicationServiceImpl implements ApplicationService {

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private ApplicationNoteRepository applicationNoteRepository;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private JobSeekerProfileRepository seekerRepository;

    @Autowired
    private ResumeRepository resumeRepository;

    @Autowired
    private EmployerProfileRepository employerRepository;

    @Override
    public Application applyJob(Long jobId,
                                Long seekerId,
                                Long resumeId,
                                String coverLetter) {

        if(applicationRepository
                .findByJobJobIdAndSeekerSeekerId(jobId, seekerId)
                .isPresent()) {

            throw new RuntimeException("Already applied for this job");
        }

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found with id: " + jobId));

        JobSeekerProfile seeker = seekerRepository.findById(seekerId)
                .orElseThrow(() -> new RuntimeException("Seeker not found with id: " + seekerId));

        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new RuntimeException("Resume not found with id: " + resumeId));

        Application application = new Application();
        application.setJob(job);
        application.setSeeker(seeker);
        application.setResume(resume);
        application.setCoverLetter(coverLetter);
        application.setStatus("APPLIED");

        return applicationRepository.save(application);
    }

    public List<ApplicationResponseDTO> getApplicationsBySeeker(Long seekerId) {

        return applicationRepository.findBySeekerSeekerId(seekerId)
                .stream()
                .map(app -> new ApplicationResponseDTO(
                        app.getApplicationId(),
                        app.getSeeker().getFullName(),
                        app.getSeeker().getUser().getEmail(),
                        app.getJob().getTitle(),
                        app.getStatus(),
                        app.getAppliedDate()
                ))
                .toList();
    }

    @Override
    public void withdrawApplication(Long applicationId, String reason) {

        Application app = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found"));

        app.setStatus("WITHDRAWN");
        app.setWithdrawReason(reason);

        applicationRepository.save(app);
    }

    @Override
    public Application updateStatus(Long applicationId, String status) {

        Application app = applicationRepository.findById(applicationId)
                .orElseThrow();

        List<String> validStatuses = List.of(
                "APPLIED", "UNDER_REVIEW",
                "SHORTLISTED", "REJECTED", "WITHDRAWN"
        );

        if(!validStatuses.contains(status)) {
            throw new RuntimeException("Invalid status value");
        }

        app.setStatus(status);
        return applicationRepository.save(app);

    }

    @Override
    public List<ApplicationResponseDTO> getApplicationsByJob(Long jobId) {

        return applicationRepository.findByJobJobId(jobId)
                .stream()
                .map(app -> new ApplicationResponseDTO(
                        app.getApplicationId(),
                        app.getSeeker().getFullName(),
                        app.getSeeker().getUser().getEmail(),
                        app.getJob().getTitle(),
                        app.getStatus(),
                        app.getAppliedDate()
                ))
                .toList();
    }

    @Override
    public List<EmployerApplicationDTO> getApplicationsByEmployer(Long employerId) {

        return applicationRepository
                .findByJobEmployerEmployerId(employerId)
                .stream()
                .map(app -> new EmployerApplicationDTO(
                        app.getApplicationId(),
                        app.getJob().getTitle(),
                        app.getJob().getJobId(),
                        app.getSeeker().getFullName(),
                        app.getSeeker().getUser().getEmail(),
                        app.getStatus(),
                        app.getAppliedDate()
                ))
                .toList();
    }

    @Override
    public String addEmployerNotes(Long applicationId,
                                   Long employerId,
                                   String noteText) {

        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found"));

        EmployerProfile employer = employerRepository.findById(employerId)
                .orElseThrow(() -> new RuntimeException("Employer not found"));

        ApplicationNote note = new ApplicationNote();
        note.setApplication(application);
        note.setEmployer(employer);
        note.setNoteText(noteText);
        note.setCreatedAt(LocalDateTime.now());

        applicationNoteRepository.save(note);

        return "Note added successfully";
    }
}