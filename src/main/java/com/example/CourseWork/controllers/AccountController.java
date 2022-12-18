package com.example.CourseWork.controllers;

import com.example.CourseWork.entity.User;
import com.example.CourseWork.security.MyUserDetails;
import com.example.CourseWork.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AccountController {

    @Autowired
    private UserService service;

    @GetMapping("/account")
    public String viewDetails(@AuthenticationPrincipal MyUserDetails loggedUser, Model model){
      String email=  loggedUser.getUsername();
      User user= service.getByEmail(email);
      model.addAttribute("user",user);


      return "account_form";
    }

    @PostMapping("/account/update")
    public String updateUser(User user, RedirectAttributes redirectAttributes){
        System.out.println(user);
        service.updateUser(user);
        redirectAttributes.addFlashAttribute("massage","Ваши данные были изменены.");
        return "redirect:/account";
    }
    @PostMapping("/account/update_password")
    public String updatePasswordUser(User user, RedirectAttributes redirectAttributes){
        System.out.println(user);
        service.savePassword(user);
        redirectAttributes.addFlashAttribute("massage","Пороль изменён успешно.");
        return "redirect:/account";
    }

}
