package com.RevHire.service.impl;

import java.util.Optional;

import com.RevHire.dto.EmployerDashboardDTO;
import com.RevHire.repository.ApplicationRepository;
import com.RevHire.repository.JobRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import com.RevHire.dto.EmployerProfileDTO;
import com.RevHire.entity.EmployerProfile;
import com.RevHire.entity.User;
import com.RevHire.repository.EmployerProfileRepository;
import com.RevHire.repository.UserRepository;
import com.RevHire.service.EmployerService;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class EmployerServiceImpl implements EmployerService {

    private final EmployerProfileRepository employerProfileRepository;
    private final UserRepository userRepository;
    private final JobRepository jobRepository;
    private final ApplicationRepository applicationRepository;

    @Override
    public EmployerProfileDTO createOrUpdateProfile(Long userId, EmployerProfileDTO dto) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User not found"));;

        EmployerProfile profile = employerProfileRepository
                .findByUserUserId(userId)
                .orElse(new EmployerProfile());

        profile.setUser(user);
        profile.setCompanyName(dto.getCompanyName());
        profile.setIndustry(dto.getIndustry());
        profile.setCompanySize(dto.getCompanySize());
        profile.setDescription(dto.getDescription());
        profile.setWebsite(dto.getWebsite());
        profile.setLocation(dto.getLocation());

        employerProfileRepository.save(profile);

        return dto;
    }

    @Override
    public EmployerProfileDTO getProfile(Long userId) {
        System.out.println("Trying to find user id: " + userId);
        System.out.println("User exists? " + userRepository.existsById(userId));
        EmployerProfile profile = employerProfileRepository
                .findByUserUserId(userId)
                .orElseThrow(() -> new RuntimeException("Profile not found"));

        EmployerProfileDTO dto = new EmployerProfileDTO();
        dto.setCompanyName(profile.getCompanyName());
        dto.setIndustry(profile.getIndustry());
        dto.setCompanySize(profile.getCompanySize());
        dto.setDescription(profile.getDescription());
        dto.setWebsite(profile.getWebsite());
        dto.setLocation(profile.getLocation());

        return dto;
    }

    @Override
    public EmployerDashboardDTO getDashboard(Long employerId) {

        Long totalJobs = jobRepository.countByEmployerEmployerId(employerId);

        Long activeJobs = jobRepository
                .countByEmployerEmployerIdAndStatus(employerId, "OPEN");

        Long totalApplications = applicationRepository
                .countByJobEmployerEmployerId(employerId);

        Long pendingReviews = applicationRepository
                .countByJobEmployerEmployerIdAndStatus(
                        employerId, "PENDING");

        return new EmployerDashboardDTO(
                totalJobs,
                activeJobs,
                totalApplications,
                pendingReviews
        );
    }
}