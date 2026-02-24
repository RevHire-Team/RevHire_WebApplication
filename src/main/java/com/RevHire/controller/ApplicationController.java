package com.RevHire.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.RevHire.service.ApplicationService;

@Controller
@RequestMapping("/applications")
public class ApplicationController {

    @Autowired
    private ApplicationService applicationService;

    @PostMapping("/apply")
    public String apply(@RequestParam Long jobId,
                        @RequestParam Long seekerId,
                        @RequestParam Long resumeId,
                        @RequestParam(required = false) String coverLetter) {

        applicationService.applyJob(jobId, seekerId, resumeId, coverLetter);
        return "redirect:/applications/seeker/" + seekerId;
    }

    @GetMapping("/seeker/{seekerId}")
    public String viewSeekerApplications(@PathVariable Long seekerId, Model model) {
        model.addAttribute("applications", applicationService.getApplicationsBySeeker(seekerId));
        return "applications";
    }

    @PostMapping("/withdraw/{id}")
    public String withdraw(@PathVariable Long id,
                           @RequestParam String reason) {

        applicationService.withdrawApplication(id, reason);
        return "redirect:/applications";
    }

    @PostMapping("/update-status/{id}")
    public String updateStatus(@PathVariable Long id,
                               @RequestParam String status) {

        applicationService.updateStatus(id, status);
        return "redirect:/applications";
    }
}