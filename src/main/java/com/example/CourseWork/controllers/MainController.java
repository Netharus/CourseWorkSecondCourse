package com.example.CourseWork.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    @GetMapping("")
    public String getUsersPage(){
        return "index";
    }

    @GetMapping("/login")
    public String getLoginPage(){
        return "login";
    }
}
