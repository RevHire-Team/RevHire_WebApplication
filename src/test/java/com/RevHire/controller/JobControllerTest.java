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

    @Test
    void testShowCreateJobPage_WithSession() throws Exception {
        User user = new User();
        user.setUserId(1L);

        mockMvc.perform(get("/jobs/create")
                        .sessionAttr("loggedInUser", user))
                .andExpect(status().isOk())
                .andExpect(view().name("employer/jobs/create-job"));
    }

    @Test
    void testShowCreateJobPage_NoSession() throws Exception {
        mockMvc.perform(get("/jobs/create"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/auth/login"));
    }

    @Test
    void testCreateJob() throws Exception {
        Job job = new Job();

        when(jobService.createJob(any(Job.class), eq(1L))).thenReturn(new Job());

        mockMvc.perform(post("/jobs/create/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(job)))
                .andExpect(status().isOk());
    }

    @Test
    void testSearchJobs() throws Exception {
        List<JobDTO> jobs = new ArrayList<>();

        when(jobService.searchJobs(any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(jobs);

        mockMvc.perform(get("/jobs/search")
                        .param("title", "Java"))
                .andExpect(status().isOk())
                .andExpect(view().name("jobs/search-results"))
                .andExpect(model().attributeExists("jobs"));
    }

    @Test
    void testShowManageJobsPage_WithSession() throws Exception {
        User user = new User();
        user.setUserId(1L);

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

    @Test
    void testGetEmployerJobs() throws Exception {
        List<JobDTO> jobs = new ArrayList<>();

        when(jobService.getEmployerJobsSorted(eq(1L), any()))
                .thenReturn(jobs);

        mockMvc.perform(get("/jobs/jobs/1")
                        .param("sort", "salary"))
                .andExpect(status().isOk());
    }

    @Test
    void testDeleteJob() throws Exception {
        doNothing().when(jobService).deleteJob(1L);

        mockMvc.perform(delete("/jobs/jobs/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Deleted successfully"));
    }

    @Test
    void testToggleJob() throws Exception {
        when(jobService.toggleJobStatus(1L)).thenReturn(mock(JobDTO.class));

        mockMvc.perform(put("/jobs/jobs/toggle/1"))
                .andExpect(status().isOk());
    }

    @Test
    void testShowEditJobPage_WithSession() throws Exception {
        User user = new User();
        user.setUserId(1L);

        mockMvc.perform(get("/jobs/jobs/edit/1")
                        .sessionAttr("loggedInUser", user))
                .andExpect(status().isOk())
                .andExpect(view().name("employer/jobs/edit-job"))
                .andExpect(model().attributeExists("jobId"));
    }

    @Test
    void testShowEditJobPage_NoSession() throws Exception {
        mockMvc.perform(get("/jobs/jobs/edit/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/auth/login"));
    }

    @Test
    void testGetJobById() throws Exception {
        when(jobService.getJobById(1L)).thenReturn(mock(JobDTO.class));

        mockMvc.perform(get("/jobs/get/1"))
                .andExpect(status().isOk());
    }

    @Test
    void testUpdateJob() throws Exception {
        Job job = new Job();

        when(jobService.updateJob(eq(1L), any(Job.class)))
                .thenReturn(mock(JobDTO.class));

        mockMvc.perform(put("/jobs/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(job)))
                .andExpect(status().isOk());
    }
}