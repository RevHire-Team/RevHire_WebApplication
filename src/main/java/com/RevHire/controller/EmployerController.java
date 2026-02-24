package com.RevHire.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class EmployerController {

    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard";
    }
}