package com.RevHire.controller;

import com.RevHire.dto.JobDTO;
import com.RevHire.entity.Job;
import com.RevHire.entity.User;
import com.RevHire.service.JobService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class JobControllerTest {

    private MockMvc mockMvc;

    @Mock
    private JobService jobService;

    @InjectMocks
    private JobController jobController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(jobController).build();
        objectMapper = new ObjectMapper();
    }

    // ==============================
    // TEST: VIEW ALL JOBS
    // ==============================
//    @Test
//    void testViewAllJobs() throws Exception {
//        List<JobDTO> jobs = new ArrayList<>();
//        when(jobService.getAllOpenJobs()).thenReturn(jobs);
//
//        mockMvc.perform(get("/jobs"))
//                .andExpect(status().isOk())
//                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
//    }

    // ==============================
    // TEST: CREATE JOB PAGE (LOGIN)
    // ==============================
    @Test
    void testShowCreateJobPage_WithSession() throws Exception {

        User user = new User();

        mockMvc.perform(get("/jobs/create")
                        .sessionAttr("loggedInUser", user))
                .andExpect(status().isOk())
                .andExpect(view().name("employer/jobs/create-job"));
    }

    // ==============================
    // TEST: CREATE JOB PAGE (NO LOGIN)
    // ==============================
    @Test
    void testShowCreateJobPage_NoSession() throws Exception {

        mockMvc.perform(get("/jobs/create"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/auth/login"));
    }

    // ==============================
    // TEST: CREATE JOB API
    // ==============================
    @Test
    void testCreateJob() throws Exception {

        Job job = new Job();

        when(jobService.createJob(any(Job.class), eq(1L))).thenReturn(new Job());

        mockMvc.perform(post("/jobs/create/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(job)))
                .andExpect(status().isOk());
    }

    // ==============================
    // TEST: SEARCH JOB
    // ==============================
    @Test
    void testSearchJobs() throws Exception {

        List<JobDTO> jobs = new ArrayList<>();

        when(jobService.searchJobs(any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(jobs);

        mockMvc.perform(get("/jobs/search")
                        .param("title", "Java"))
                .andExpect(status().isOk());
    }

    // ==============================
    // TEST: CLOSE JOB
    // ==============================
    // ==============================
// TEST: CLOSE JOB
// ==============================
//    @Test
//    void testCloseJob() throws Exception {
//
//        // Mock the service
//        doNothing().when(jobService).closeJob(1L);
//
//        // Perform PUT request and assert the response string
//        mockMvc.perform(put("/jobs/close/1")
//                        .accept(MediaType.TEXT_PLAIN))
//                .andExpect(status().isOk())
//                .andExpect(content().string("Job Closed Successfully")); // match exact response
//    }

    // ==============================
    // TEST: MANAGE JOB PAGE
    // ==============================
    @Test
    void testShowManageJobsPage_WithSession() throws Exception {

        User user = new User();

        mockMvc.perform(get("/jobs/manage")
                        .sessionAttr("loggedInUser", user))
                .andExpect(status().isOk())
                .andExpect(view().name("employer/jobs/manage-jobs"));
    }

    @Test
    void testShowManageJobsPage_NoSession() throws Exception {

        mockMvc.perform(get("/jobs/manage"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/auth/login"));
    }

    // ==============================
    // TEST: GET EMPLOYER JOBS
    // ==============================
    @Test
    void testGetEmployerJobs() throws Exception {

        List<JobDTO> jobs = new ArrayList<>();

        when(jobService.getJobsByUserId(1L)).thenReturn(jobs);

        mockMvc.perform(get("/jobs/jobs/1"))
                .andExpect(status().isOk());
    }

    // ==============================
    // TEST: DELETE JOB
    // ==============================
    @Test
    void testDeleteJob() throws Exception {

        doNothing().when(jobService).deleteJob(1L);

        mockMvc.perform(delete("/jobs/jobs/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Deleted successfully"));
    }

    // ==============================
    // TEST: TOGGLE JOB STATUS
    // ==============================
    @Test
    void testToggleJob() throws Exception {

        when(jobService.toggleJobStatus(1L)).thenReturn(mock(JobDTO.class));

        mockMvc.perform(put("/jobs/jobs/toggle/1"))
                .andExpect(status().isOk());
    }

    // ==============================
    // TEST: EDIT JOB PAGE
    // ==============================
    @Test
    void testShowEditJobPage() throws Exception {

        User user = new User();

        mockMvc.perform(get("/jobs/jobs/edit/1")
                        .sessionAttr("loggedInUser", user))
                .andExpect(status().isOk())
                .andExpect(view().name("employer/jobs/edit-job"))
                .andExpect(model().attributeExists("jobId"));
    }

    // ==============================
    // TEST: GET JOB BY ID
    // ==============================
    @Test
    void testGetJobById() throws Exception {

        when(jobService.getJobById(1L)).thenReturn(mock(JobDTO.class));

        mockMvc.perform(get("/jobs/get/1"))
                .andExpect(status().isOk());
    }

    // ==============================
    // TEST: UPDATE JOB
    // ==============================
    @Test
    void testUpdateJob() throws Exception {

        Job job = new Job();

        when(jobService.updateJob(eq(1L), any(Job.class))).thenReturn(mock(JobDTO.class));

        mockMvc.perform(put("/jobs/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(job)))
                .andExpect(status().isOk());
    }
}