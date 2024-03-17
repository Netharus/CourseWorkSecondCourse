package com.example.CourseWork.controllers;

import com.example.CourseWork.entity.Category;
import com.example.CourseWork.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class MainController {

    @Autowired
    private CategoryService categoryService;
    @GetMapping("/")
    public String getUsersPage(Model model){
        List<Category> categoryList= categoryService.listNoChildrenCategories();

        model.addAttribute("categoryList",categoryList);
        return "index";
    }

    @GetMapping("/login")
    public String getLoginPage(){
        return "login";
    }
}
