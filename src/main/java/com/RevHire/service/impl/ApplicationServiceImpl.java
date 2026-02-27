package com.RevHire.service.impl;

import java.util.List;

import com.RevHire.dto.ApplicationResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.RevHire.entity.Application;
import com.RevHire.entity.Job;
import com.RevHire.entity.JobSeekerProfile;
import com.RevHire.entity.Resume;
import com.RevHire.repository.ApplicationRepository;
import com.RevHire.repository.JobRepository;
import com.RevHire.repository.JobSeekerProfileRepository;
import com.RevHire.repository.ResumeRepository;
import com.RevHire.service.ApplicationService;

@Service
public class ApplicationServiceImpl implements ApplicationService {

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private JobSeekerProfileRepository seekerRepository;

    @Autowired
    private ResumeRepository resumeRepository;

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
                        app.getStatus()
                ))
                .toList();
    }

    @Override
    public List<Application> getApplicationsByEmployer(Long employerId) {
        return applicationRepository.findByJobEmployerEmployerId(employerId);
    }

//    @Override
//    public Application addEmployerNotes(Long applicationId, String notes) {
//
//        Application app = applicationRepository.findById(applicationId)
//                .orElseThrow(() -> new RuntimeException("Application not found"));
//
//        app.setEmployerNotes(notes);
//        return applicationRepository.save(app);
//    }
}