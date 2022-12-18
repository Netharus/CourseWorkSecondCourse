package com.example.CourseWork.controllers;

import com.example.CourseWork.entity.Role;
import com.example.CourseWork.entity.User;
import com.example.CourseWork.exceptions.UserNotFoundException;
import com.example.CourseWork.export.UserExcelExporter;
import com.example.CourseWork.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;

@Controller
public class UserController {

    @Autowired
    private UserService service;

    @GetMapping("/users")
    public String listFirstPage(Model model){
        List<User> listUsers= service.listAll();
        model.addAttribute("listUsers",listUsers);
        return listByPage(1,model,"firstName","asc",null);
    }
    @GetMapping("/users/new")
    public String newUser(Model model){
        List<Role> listRoles=service.listRoles();

        User user=new User();
        user.setEnabled(true);
        model.addAttribute("user",user);
        model.addAttribute("listRoles",listRoles);
        model.addAttribute("pageTitle","Создание нового пользователя");
        return "user_form";
    }
    @PostMapping("/users/save")
    public String saveUser(User user, RedirectAttributes redirectAttributes){
        System.out.println(user);
        service.save(user);
        redirectAttributes.addFlashAttribute("massage","Пользователь сохранён успешно!");
        return "redirect:/users";
    }
    @GetMapping("/users/page/{pageNum}")
    public String listByPage(@PathVariable(name="pageNum") int pageNum, Model model,
         @Param("sortField") String sortField, @Param("sortDir") String sortDir,@Param("keyword") String keyword){
        Page<User> page =service.listByPage(pageNum,sortField,sortDir,keyword);
        List<User> listUsers=page.getContent();
        Integer currentPage=pageNum;
        Long startCount= Long.valueOf((pageNum-1)* UserService.USERS_PER_PAGE+1);
        Long endCount=startCount+UserService.USERS_PER_PAGE-1;
        if(endCount>page.getTotalElements()){
            endCount=page.getTotalElements();
        }

        String reverseSortDir= sortDir.equals("asc")?"desc":"asc";

        model.addAttribute("keyword",keyword);
        model.addAttribute("currentPage",currentPage);
        model.addAttribute("startCount",startCount);
        model.addAttribute("endCount",endCount);
        model.addAttribute("totalItems",page.getTotalElements());
        model.addAttribute("totalPages",page.getTotalPages());
        model.addAttribute("listUsers",listUsers);
        model.addAttribute("sortField",sortField);
        model.addAttribute("sortDir",sortDir);
        model.addAttribute("reverseSortDir",reverseSortDir);
        return "users";
    }

    @GetMapping("/users/edit/{id}")
    public String editUser(@PathVariable(name="id") Long id,RedirectAttributes redirectAttributes,Model model){
        try{
        User user=service.get(id);
        List<Role> listRoles=service.listRoles();
        model.addAttribute("user",user);
        model.addAttribute("listRoles",listRoles);
        model.addAttribute("pageTitle","Редактирование пользователя (ID: "+user.getId()+")");
            return "user_form";
    }catch(UserNotFoundException ex){
            redirectAttributes.addFlashAttribute("massage",ex.getMessage());
        }

        return "redirect:/users";
    }
    @GetMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable(name="id") Long id,RedirectAttributes redirectAttributes,Model model){
        try{
            service.delete(id);
            redirectAttributes.addFlashAttribute("massage","Пользователь был успешно удалён");

        }catch(UserNotFoundException ex){
            redirectAttributes.addFlashAttribute("massage",ex.getMessage());
        }

        return "redirect:/users";
    }
    @GetMapping("/users/change_status/{id}")
    public String change_status(@PathVariable(name="id") Long id,RedirectAttributes redirectAttributes,Model model){
        try{
            service.changeStatus(id);
            redirectAttributes.addFlashAttribute("massage","Статус изменён успешно");

        }catch(UserNotFoundException ex){
            redirectAttributes.addFlashAttribute("massage",ex.getMessage());
        }

        return "redirect:/users";
    }

    @GetMapping("/users/export/excel")
    public void exportToExcel(HttpServletResponse response) throws IOException {
        List<User> listUsers = service.listAll();

        UserExcelExporter exporter = new UserExcelExporter();

        exporter.export(listUsers,response);
    }
}
