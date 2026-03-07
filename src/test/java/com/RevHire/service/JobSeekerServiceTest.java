package com.RevHire.service;

import com.RevHire.dto.FavoriteJobDTO;
import com.RevHire.entity.*;
import com.RevHire.repository.*;
import com.RevHire.service.impl.JobSeekerServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JobSeekerServiceTest {

    @Mock
    private JobSeekerProfileRepository profileRepo;

    @Mock
    private ResumeRepository resumeRepo;

    @Mock
    private ResumeFileRepository resumeFileRepo;

    @Mock
    private FavoriteJobRepository favoriteJobRepo;

    @Mock
    private NotificationRepository notificationRepo;

    @Mock
    private UserRepository userRepo;

    @Mock
    private JobRepository jobRepo;

    @InjectMocks
    private JobSeekerServiceImpl jobSeekerService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    // -------- Profile --------

    @Test
    void testCreateProfile() {

        User user = new User();
        user.setUserId(1L);

        JobSeekerProfile profile = new JobSeekerProfile();
        profile.setFullName("John");

        when(userRepo.findById(1L)).thenReturn(Optional.of(user));
        when(profileRepo.save(any())).thenReturn(profile);

        JobSeekerProfile result =
                jobSeekerService.createProfile(profile,1L);

        assertEquals("John",result.getFullName());
    }

    @Test
    void testGetProfile() {

        JobSeekerProfile profile = new JobSeekerProfile();
        profile.setFullName("David");

        when(profileRepo.findByUserUserId(1L))
                .thenReturn(Optional.of(profile));

        Optional<JobSeekerProfile> result =
                jobSeekerService.getProfile(1L);

        assertTrue(result.isPresent());
    }

    @Test
    void testUpdateProfile() {

        JobSeekerProfile existing = new JobSeekerProfile();
        existing.setSeekerId(1L);
        existing.setFullName("Old");

        JobSeekerProfile updated = new JobSeekerProfile();
        updated.setFullName("New");

        when(profileRepo.findById(1L))
                .thenReturn(Optional.of(existing));

        when(profileRepo.save(existing)).thenReturn(existing);

        JobSeekerProfile result =
                jobSeekerService.updateProfile(1L,updated);

        assertEquals("New",result.getFullName());
    }

    // -------- Favorite Jobs --------

    @Test
    void testAddFavoriteJob() {

        JobSeekerProfile seeker = new JobSeekerProfile();
        seeker.setSeekerId(1L);

        EmployerProfile employer = new EmployerProfile();
        employer.setCompanyName("Google");

        Job job = new Job();
        job.setJobId(2L);
        job.setEmployer(employer);

        when(profileRepo.findById(1L))
                .thenReturn(Optional.of(seeker));

        when(jobRepo.findById(2L))
                .thenReturn(Optional.of(job));

        when(favoriteJobRepo.existsBySeekerSeekerIdAndJobJobId(1L,2L))
                .thenReturn(false);

        FavoriteJob fav = new FavoriteJob();

        when(favoriteJobRepo.save(any())).thenReturn(fav);

        FavoriteJob result =
                jobSeekerService.addFavoriteJob(1L,2L);

        assertNotNull(result);
    }

    @Test
    void testGetFavorites() {

        JobSeekerProfile seeker = new JobSeekerProfile();

        EmployerProfile employer = new EmployerProfile();
        employer.setCompanyName("Amazon");

        Job job = new Job();
        job.setJobId(1L);
        job.setTitle("Java Developer");
        job.setLocation("Bangalore");
        job.setSalaryMin(BigDecimal.valueOf(50000));
        job.setSalaryMax(BigDecimal.valueOf(80000));
        job.setJobType("FULL_TIME");
        job.setStatus("OPEN");
        job.setEmployer(employer);

        FavoriteJob fav = new FavoriteJob();
        fav.setFavId(1L);
        fav.setJob(job);

        when(favoriteJobRepo.findBySeekerSeekerId(1L))
                .thenReturn(List.of(fav));

        List<FavoriteJobDTO> result =
                jobSeekerService.getFavorites(1L);

        assertEquals(1,result.size());
    }

    @Test
    void testRemoveFavoriteJob() {

        jobSeekerService.removeFavoriteJob(1L);

        verify(favoriteJobRepo).deleteById(1L);
    }

    // -------- Notification --------

    @Test
    void testMarkNotificationAsRead() {

        Notification notification = new Notification();
        notification.setNotificationId(1L);
        notification.setIsRead(false);

        when(notificationRepo.findById(1L))
                .thenReturn(Optional.of(notification));

        jobSeekerService.markNotificationAsRead(1L);

        assertTrue(notification.getIsRead());
    }
}