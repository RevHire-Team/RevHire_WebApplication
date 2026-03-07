package com.RevHire.service;

import com.RevHire.dto.EmployerDashboardDTO;
import com.RevHire.dto.EmployerProfileDTO;
import com.RevHire.entity.EmployerProfile;
import com.RevHire.entity.User;
import com.RevHire.repository.ApplicationRepository;
import com.RevHire.repository.EmployerProfileRepository;
import com.RevHire.repository.JobRepository;
import com.RevHire.repository.UserRepository;
import com.RevHire.service.impl.EmployerServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EmployerServiceTest {

    @Mock
    private EmployerProfileRepository employerProfileRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JobRepository jobRepository;

    @Mock
    private ApplicationRepository applicationRepository;

    @InjectMocks
    private EmployerServiceImpl employerService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateOrUpdateProfile() {

        User user = new User();
        user.setUserId(1L);

        EmployerProfileDTO dto = new EmployerProfileDTO();
        dto.setCompanyName("Google");
        dto.setIndustry("IT");
        dto.setLocation("Bangalore");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(employerProfileRepository.findByUserUserId(1L))
                .thenReturn(Optional.empty());

        EmployerProfile result = new EmployerProfile();
        when(employerProfileRepository.save(any())).thenReturn(result);

        EmployerProfileDTO response =
                employerService.createOrUpdateProfile(1L, dto);

        assertEquals("Google", response.getCompanyName());
    }

    @Test
    void testGetProfile() {

        User user = new User();
        user.setUserId(1L);

        EmployerProfile profile = new EmployerProfile();
        profile.setCompanyName("Microsoft");
        profile.setIndustry("Software");
        profile.setLocation("Hyderabad");

        when(userRepository.existsById(1L)).thenReturn(true);
        when(employerProfileRepository.findByUserUserId(1L))
                .thenReturn(Optional.of(profile));

        EmployerProfileDTO dto = employerService.getProfile(1L);

        assertEquals("Microsoft", dto.getCompanyName());
    }

    @Test
    void testGetDashboard() {

        when(jobRepository.countByEmployerEmployerId(1L)).thenReturn(5L);
        when(jobRepository.countByEmployerEmployerIdAndStatus(1L,"OPEN"))
                .thenReturn(3L);

        when(applicationRepository.countByJobEmployerEmployerId(1L))
                .thenReturn(20L);

        when(applicationRepository
                .countByJobEmployerEmployerIdAndStatus(1L,"PENDING"))
                .thenReturn(4L);

        EmployerDashboardDTO dashboard =
                employerService.getDashboard(1L);

        assertEquals(5L,dashboard.getTotalJobs());
        assertEquals(3L,dashboard.getActiveJobs());
        assertEquals(20L,dashboard.getTotalApplications());
    }
}