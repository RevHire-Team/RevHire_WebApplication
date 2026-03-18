package com.RevHire.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import com.RevHire.dto.ApplicationResponseDTO;
import com.RevHire.dto.EmployerApplicationDTO;
import com.RevHire.entity.*;
import com.RevHire.repository.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.RevHire.service.NotificationService;

import com.RevHire.service.ApplicationService;

@Service
public class ApplicationServiceImpl implements ApplicationService {

    private static final Logger logger = LogManager.getLogger(ApplicationServiceImpl.class);

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
    private NotificationService notificationService;

    @Autowired
    private EmployerProfileRepository employerRepository;

    @Override
    public Application applyJob(Long jobId,
                                Long userId,
                                Long resumeId,
                                String coverLetter) {

        logger.info("Attempting to apply for jobId: {} by seekerId: {}", jobId, userId);

        if(applicationRepository
                .findByJobJobIdAndSeekerSeekerId(jobId, userId)
                .isPresent()) {

            logger.warn("Seeker {} already applied for job {}", userId, jobId);
            throw new RuntimeException("Already applied for this job");
        }

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> {
                    logger.error("Job not found with id: {}", jobId);
                    return new RuntimeException("Job not found with id: " + jobId);
                });

        JobSeekerProfile seeker = seekerRepository.findByUserUserId(userId)
                .orElseThrow(() -> {
                    logger.error("Seeker not found with id: {}", userId);
                    return new RuntimeException("Seeker not found with id: " + userId);
                });
        Long seekerId=seeker.getSeekerId();

        Resume resume = resumeRepository.findBySeeker_SeekerId(seekerId)
                .orElseThrow(() -> {
                    logger.error("Resume not found for seeker {}", userId);
                    return new RuntimeException("Resume not found for seeker");
                });

        /* Validate resume belongs to seeker */
        if (!resume.getSeeker().getSeekerId().equals(seekerId)) {

            logger.error("Resume {} does not belong to seeker {}", resumeId, seekerId);

            throw new RuntimeException("Invalid resume selected for this seeker");
        }

        Application application = new Application();
        application.setJob(job);
        application.setSeeker(seeker);
        application.setResume(resume);
        application.setCoverLetter(coverLetter);
        application.setStatus("APPLIED");

        Application saved = applicationRepository.save(application);

        /* SEND NOTIFICATION TO EMPLOYER */

        Long employerUserId = job.getEmployer().getUser().getUserId();

        String message =
                seeker.getFullName() +
                        " applied for " +
                        job.getTitle();

        notificationService.sendNotification(employerUserId, message);

        return saved;


    }

    public List<ApplicationResponseDTO> getApplicationsBySeeker(Long seekerId) {

        logger.info("Fetching applications for userId: {}", seekerId);

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

        logger.info("Withdraw request received for applicationId: {}", applicationId);

        Application app = applicationRepository.findById(applicationId)
                .orElseThrow(() -> {
                    logger.error("Application not found with id: {}", applicationId);
                    return new RuntimeException("Application not found");
                });

        app.setStatus("WITHDRAWN");
        app.setWithdrawReason(reason);

        applicationRepository.save(app);

        logger.info("Application {} withdrawn successfully", applicationId);
    }

    @Override
    public Application updateStatus(Long applicationId, String status) {

        logger.info("Updating status for applicationId: {} to {}", applicationId, status);

        Application app = applicationRepository.findById(applicationId)
                .orElseThrow(() -> {
                    logger.error("Application not found with id: {}", applicationId);
                    return new RuntimeException("Application not found");
                });

        List<String> validStatuses = List.of(
                "APPLIED", "UNDER_REVIEW",
                "SHORTLISTED", "REJECTED", "WITHDRAWN"
        );

        if(!validStatuses.contains(status)) {
            logger.warn("Invalid status attempted: {}", status);
            throw new RuntimeException("Invalid status value");
        }

        app.setStatus(status);

        Application updated = applicationRepository.save(app);

        /* SEND NOTIFICATION TO JOB SEEKER */

        Long seekerUserId = app.getSeeker().getUser().getUserId();

        String message;

        if(status.equals("SHORTLISTED")){

            message = "Your application for "
                    + app.getJob().getTitle()
                    + " was SHORTLISTED";

        }else if(status.equals("REJECTED")){

            message = "Your application for "
                    + app.getJob().getTitle()
                    + " was REJECTED";

        }else{
            message = "Your application status updated to " + status;
        }

        notificationService.sendNotification(seekerUserId, message);

        Application updatedApplication = applicationRepository.save(app);

        logger.info("Application status updated successfully for applicationId: {}", applicationId);

        return updatedApplication;
    }

    @Override
    public List<ApplicationResponseDTO> getApplicationsByJob(Long jobId) {

        logger.info("Fetching applications for jobId: {}", jobId);

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
    public List<EmployerApplicationDTO> getApplicationsByEmployer(Long userId) {

        logger.info("Fetching applications for employer userId: {}", userId);

        EmployerProfile employer = employerRepository.findByUserUserId(userId)
                .orElseThrow(() -> {
                    logger.error("Employer profile not found for userId: {}", userId);
                    return new RuntimeException("Employer Profile not found for User ID: " + userId);
                });

        return applicationRepository
                .findByJobEmployerEmployerId(employer.getEmployerId())
                .stream()
                .map(app -> new EmployerApplicationDTO(
                        app.getApplicationId(),
                        app.getJob().getTitle(),
                        app.getJob().getJobId(),
                        app.getSeeker().getFullName(),
                        app.getSeeker().getUser().getEmail(),
                        app.getStatus(),
                        app.getAppliedDate(),
                        app.getResume() != null ? app.getResume().getResumeId() : null
                ))
                .toList();
    }

    @Override
    public String addEmployerNotes(Long applicationId,
                                   Long employerId,
                                   String noteText) {

        logger.info("Adding employer note for applicationId: {}", applicationId);

        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> {
                    logger.error("Application not found with id: {}", applicationId);
                    return new RuntimeException("Application not found");
                });

        EmployerProfile employer = employerRepository.findById(employerId)
                .orElseThrow(() -> {
                    logger.error("Employer not found with id: {}", employerId);
                    return new RuntimeException("Employer not found");
                });

        ApplicationNote note = new ApplicationNote();
        note.setApplication(application);
        note.setEmployer(employer);
        note.setNoteText(noteText);
        note.setCreatedAt(LocalDateTime.now());

        applicationNoteRepository.save(note);

        logger.info("Employer note added successfully for applicationId: {}", applicationId);

        return "Note added successfully";
    }

}