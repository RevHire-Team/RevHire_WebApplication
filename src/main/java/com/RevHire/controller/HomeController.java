package com.RevHire.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import com.RevHire.entity.User;

@Controller
public class HomeController {

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }

    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        model.addAttribute("user", new User());
        return "auth/register";
    }

//    @GetMapping("/register-employer")
//    public String registerEmployer(Model model) {
//        model.addAttribute("user", new User());  // if using th:object
//        return "auth/registeremployer";
//    }
}