package com.RevHire.controller;

import java.util.List;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.RevHire.dto.JobDTO;
import com.RevHire.entity.Job;
import com.RevHire.service.JobService;

@Controller
@RequestMapping("/jobs")
public class JobController {

    @Autowired
    private JobService jobService;

    @GetMapping
    public List<JobDTO> viewAllJobs() {
        return jobService.getAllOpenJobs();
    }

    @GetMapping("/create")
    public String showCreateJobPage(HttpSession session, Model model) {

        if (session.getAttribute("loggedInUser") == null) {
            return "redirect:/auth/login";
        }

        return "employer/jobs/create-job";
    }

    @PostMapping("/create/{userId}")
    @ResponseBody
    // Spring treats the return as JSON instead of a view
    public ResponseEntity<?> createJob(
            @PathVariable Long userId,
            @RequestBody Job job) {

        return ResponseEntity.ok(jobService.createJob(job, userId));
    }

    @GetMapping("/search")
    public List<JobDTO> searchJobs(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) Integer experience,
            @RequestParam(required = false) String companyName,
            @RequestParam(required = false) Double minSalary,
            @RequestParam(required = false) Double maxSalary,
            @RequestParam(required = false) String jobType) {

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

    // Mapping for the manage jobs HTML page
    @GetMapping("/manage")
    public String showManageJobsPage(HttpSession session) {

        if (session.getAttribute("loggedInUser") == null) {
            return "redirect:/auth/login";
        }

        return "employer/jobs/manage-jobs";
    }

    // API to get jobs by employer
    @GetMapping("/jobs/{userId}")
    @ResponseBody
    public ResponseEntity<List<JobDTO>> getEmployerJobs(@PathVariable Long userId) {

        return ResponseEntity.ok(jobService.getJobsByUserId(userId));
    }

    @DeleteMapping("/jobs/{jobId}")
    public ResponseEntity<?> deleteJob(@PathVariable Long jobId) {

        jobService.deleteJob(jobId);
        return ResponseEntity.ok("Deleted successfully");
    }

    @PutMapping("/jobs/toggle/{jobId}")
    public ResponseEntity<JobDTO> toggleJob(@PathVariable Long jobId) {

        return ResponseEntity.ok(jobService.toggleJobStatus(jobId));
    }

    // Show Edit Job Page
    @GetMapping("/jobs/edit/{jobId}")
    public String showEditJobPage(
            @PathVariable Long jobId,
            Model model,
            HttpSession session) {

        if (session.getAttribute("loggedInUser") == null) {
            return "redirect:/auth/login";
        }

        model.addAttribute("jobId", jobId);

        return "employer/jobs/edit-job";
    }

    @GetMapping("/get/{jobId}")
    @ResponseBody
    public ResponseEntity<JobDTO> getJobById(@PathVariable Long jobId) {

        return ResponseEntity.ok(jobService.getJobById(jobId));
    }

    @PutMapping("/update/{jobId}")
    @ResponseBody
    public ResponseEntity<?> updateJob(
            @PathVariable Long jobId,
            @RequestBody Job job) {

        return ResponseEntity.ok(jobService.updateJob(jobId, job));
    }
}