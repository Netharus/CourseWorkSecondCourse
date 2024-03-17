package com.example.CourseWork.controllers;

import com.example.CourseWork.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AccountRestController {
    @Autowired
    private UserService service;

    @PostMapping("/account/check_email")
    public String checkDuplicateEmail(@Param("id") Long id, @Param("email") String email){
        return service.isEmailUnique(id,email)?"OK":"Duplicated";
    }

}
