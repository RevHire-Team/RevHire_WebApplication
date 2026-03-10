package com.RevHire.service;

import com.RevHire.dto.EmployerDashboardDTO;
import com.RevHire.dto.EmployerProfileDTO;

public interface EmployerService {

    EmployerProfileDTO createOrUpdateProfile(Long userId, EmployerProfileDTO dto);

    EmployerProfileDTO getProfile(Long userId);

    EmployerDashboardDTO getDashboard(Long employerId);

}