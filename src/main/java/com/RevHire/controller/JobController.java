package com.RevHire.controller;

import java.util.List;

import com.RevHire.dto.JobDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.RevHire.entity.Job;
import com.RevHire.service.JobService;

@RestController
@RequestMapping("/jobs")
public class JobController {

    @Autowired
    private JobService jobService;
    
    @GetMapping
    public List<JobDTO> viewAllJobs() {
        return jobService.getAllOpenJobs();
    }

    @PostMapping("/create")
    public Job createJob(@RequestBody Job job) {
        return jobService.createJob(job);
    }

    @GetMapping("/search")
    public List<JobDTO> searchJobs(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) Integer experience,
            @RequestParam(required = false) String companyName,
            @RequestParam(required = false) Double minSalary,
            @RequestParam(required = false) Double maxSalary,
            @RequestParam(required = false) String jobType
    ) {
        return jobService.searchJobs(
                title,
                location,
                experience,
                companyName,
                minSalary,
                maxSalary,
                jobType
        );
    }

    @PutMapping("/close/{id}")
    public String closeJob(@PathVariable Long id) {
        jobService.closeJob(id);
        return "Job Closed Successfully";
    }

    @GetMapping("/jobs/{employerId}")
    public ResponseEntity<List<JobDTO>> getEmployerJobs(@PathVariable Long employerId) {
        return ResponseEntity.ok(jobService.getEmployerJobs(employerId));
    }

    @DeleteMapping("/{jobId}")
    public ResponseEntity<?> deleteJob(@PathVariable Long jobId) {
        jobService.deleteJob(jobId);
        return ResponseEntity.ok("Deleted successfully");
    }

    @PutMapping("/jobs/toggle/{jobId}")
    public ResponseEntity<JobDTO> toggleJob(@PathVariable Long jobId) {
        return ResponseEntity.ok(jobService.toggleJobStatus(jobId));
    }
}