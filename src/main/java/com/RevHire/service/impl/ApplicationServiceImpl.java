package com.RevHire.service.impl;

import java.util.List;

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
    public Application applyJob(Long jobId, Long seekerId, Long resumeId, String coverLetter) {

        if(applicationRepository.findByJobJobIdAndSeekerSeekerId(jobId, seekerId).isPresent()) {
            throw new RuntimeException("Already applied for this job");
        }

        Job job = jobRepository.findById(jobId).orElseThrow();
        JobSeekerProfile seeker = seekerRepository.findById(seekerId).orElseThrow();
        Resume resume = resumeRepository.findById(resumeId).orElseThrow();

        Application application = new Application();
        application.setJob(job);
        application.setSeeker(seeker);
        application.setResume(resume);
        application.setCoverLetter(coverLetter);
        application.setStatus("APPLIED");

        return applicationRepository.save(application);
    }

    @Override
    public List<Application> getApplicationsBySeeker(Long seekerId) {
        return applicationRepository.findBySeekerSeekerId(seekerId);
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