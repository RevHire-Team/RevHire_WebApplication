package com.RevHire.service.impl;

import java.util.Optional;

import com.RevHire.dto.EmployerDashboardDTO;
import com.RevHire.repository.ApplicationRepository;
import com.RevHire.repository.JobRepository;
import jakarta.transaction.Transactional;
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
    @Transactional // Highly recommended: ensures both User and Profile update or neither does
    public EmployerProfileDTO createOrUpdateProfile(Long userId, EmployerProfileDTO dto) {

        // 1. Fetch User
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User not found"));

        // 2. Fetch or Create Profile
        EmployerProfile profile = employerProfileRepository
                .findByUserUserId(userId)
                .orElse(new EmployerProfile());

        // 3. Update User Email (Source of Truth)
        // Optional: Only update if the email is actually provided in the DTO
        if (dto.getContactEmail() != null && !dto.getContactEmail().isEmpty()) {
            user.setEmail(dto.getContactEmail());
            try {
                userRepository.save(user);
            } catch (Exception e) {
                // Catches "Unique Constraint" violations if email exists for another user
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already in use by another account");
            }
        }

        // 4. Update Profile Fields
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
        // 1. Find the profile
        EmployerProfile profile = employerProfileRepository
                .findByUserUserId(userId)
                .orElseThrow(() -> new RuntimeException("Profile not found"));

        // 2. Map fields to DTO
        EmployerProfileDTO dto = new EmployerProfileDTO();
        dto.setCompanyName(profile.getCompanyName());
        dto.setIndustry(profile.getIndustry());
        dto.setCompanySize(profile.getCompanySize());
        dto.setDescription(profile.getDescription());
        dto.setWebsite(profile.getWebsite());
        dto.setLocation(profile.getLocation());

        // 3. FETCH EMAIL FROM THE LINKED USER ENTITY
        if (profile.getUser() != null) {
            dto.setContactEmail(profile.getUser().getEmail());
        }

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