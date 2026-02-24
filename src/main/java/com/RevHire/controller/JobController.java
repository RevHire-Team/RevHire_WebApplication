package com.RevHire.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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
    public String viewAllJobs(Model model) {
        model.addAttribute("jobs", jobService.getAllOpenJobs());
        return "jobs";
    }

    @PostMapping("/create")
    public String createJob(@ModelAttribute Job job) {
        jobService.createJob(job);
        return "redirect:/jobs";
    }

    @GetMapping("/search")
    public String searchJobs(@RequestParam(required = false) String location,
                             @RequestParam(required = false) String title,
                             @RequestParam(required = false) String jobType,
                             Model model) {

        List<Job> results = jobService.searchJobs(location, title, jobType);
        model.addAttribute("jobs", results);
        return "jobs";
    }

    @PostMapping("/close/{id}")
    public String closeJob(@PathVariable Long id) {
        jobService.closeJob(id);
        return "redirect:/jobs";
    }
}