package com.RevHire.controller;

import java.util.List;

import com.RevHire.dto.JobDTO;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.RevHire.entity.Job;
import com.RevHire.service.JobService;

@Controller
@RequestMapping("/jobs")
public class JobController {

    @Autowired
    private JobService jobService;

    @GetMapping
    @ResponseBody
    public List<JobDTO> viewAllJobs() {
        return jobService.getAllOpenJobs();
    }

    @GetMapping("/create")
    public String showCreateJobPage(HttpSession session, Model model) {
        if (session.getAttribute("loggedInUser") == null) return "redirect:/auth/login";
        return "employer/jobs/create-job";
    }

    @PostMapping("/create/{userId}")
    @ResponseBody
    public ResponseEntity<?> createJob(@PathVariable Long userId, @RequestBody Job job) {
        return ResponseEntity.ok(jobService.createJob(job, userId));
    }

// ========================= SEARCH JOBS =========================

    @GetMapping("/search")
    public String searchJobs(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) Integer experience,
            @RequestParam(required = false) String companyName,
            @RequestParam(required = false) Double minSalary,
            @RequestParam(required = false) Double maxSalary,
            @RequestParam(required = false) String jobType,
            @RequestParam(required = false) String sort,
            Model model
    ) {

        List<JobDTO> jobs = new java.util.ArrayList<>(jobService.searchJobs(
                title,
                location,
                experience,
                companyName,
                minSalary,
                maxSalary,
                jobType
        ));

        if (sort != null) {

            switch (sort) {

                case "company":
                    jobs.sort((a, b) ->
                            a.getCompanyName().compareToIgnoreCase(b.getCompanyName())
                    );
                    break;

                case "salary":
                    jobs.sort((a, b) -> {
                        if (a.getSalaryMax() == null && b.getSalaryMax() == null) return 0;
                        if (a.getSalaryMax() == null) return -1;
                        if (b.getSalaryMax() == null) return 1;
                        return a.getSalaryMax().compareTo(b.getSalaryMax());
                    });
                    break;

                case "jobType":
                    jobs.sort((a, b) ->
                            a.getJobType().compareToIgnoreCase(b.getJobType())
                    );
                    break;
            }
        }

        model.addAttribute("jobs", jobs);

        return "jobseeker/search-jobs";
    }

// ========================= CLOSE JOB =========================

    @PutMapping("/close/{id}")
    @ResponseBody
    public String closeJob(@PathVariable Long id) {
        jobService.closeJob(id);
        return "Job Closed Successfully";
    }

// ========================= MANAGE JOB PAGE =========================

    @GetMapping("/manage")
    public String showManageJobsPage(HttpSession session) {
        if (session.getAttribute("loggedInUser") == null) return "redirect:/auth/login";
        return "employer/jobs/manage-jobs";
    }

    @GetMapping("/jobs/{userId}")
    @ResponseBody
    public ResponseEntity<List<JobDTO>> getEmployerJobs(@PathVariable Long userId) {
        return ResponseEntity.ok(jobService.getJobsByUserId(userId));
    }

    @DeleteMapping("/jobs/{jobId}")
    @ResponseBody
    public ResponseEntity<?> deleteJob(@PathVariable Long jobId) {
        jobService.deleteJob(jobId);
        return ResponseEntity.ok("Deleted successfully");
    }

    @PutMapping("/jobs/toggle/{jobId}")
    @ResponseBody
    public ResponseEntity<JobDTO> toggleJobStatus(@PathVariable Long jobId) {
        return ResponseEntity.ok(jobService.toggleJobStatus(jobId));
    }

// ========================= EDIT JOB =========================

    @GetMapping("/jobs/edit/{jobId}")
    public String showEditJobPage(@PathVariable Long jobId, Model model, HttpSession session) {
        if (session.getAttribute("loggedInUser") == null) return "redirect:/auth/login";
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
    public ResponseEntity<?> updateJob(@PathVariable Long jobId, @RequestBody Job job) {
        return ResponseEntity.ok(jobService.updateJob(jobId, job));
    }
    @GetMapping("/recommended/{skill}")
    @ResponseBody
    public ResponseEntity<List<JobDTO>> getRecommendedJobs(@PathVariable String skill) {

        List<JobDTO> jobs = jobService.getRecommendedJobs(skill);

        return ResponseEntity.ok(jobs);
    }


}
