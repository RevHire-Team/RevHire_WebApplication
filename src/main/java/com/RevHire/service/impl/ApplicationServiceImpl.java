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
                .map(app -> {
                    ApplicationResponseDTO dto = new ApplicationResponseDTO();
                    dto.setApplicationId(app.getApplicationId());
                    dto.setJobTitle(app.getJob().getTitle());
                    dto.setStatus(app.getStatus());
                    dto.setAppliedDate(app.getAppliedDate());
                    return dto;
                })
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
    public void updateStatus(Long applicationId, String status) {

        Application app = applicationRepository.findById(applicationId)
                .orElseThrow();

        app.setStatus(status);
        applicationRepository.save(app);
    }
}