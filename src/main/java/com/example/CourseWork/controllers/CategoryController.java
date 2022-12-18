package com.example.CourseWork.controllers;

import com.example.CourseWork.entity.Category;
import com.example.CourseWork.exceptions.CategoryNotFoundException;
import com.example.CourseWork.exceptions.UserNotFoundException;
import com.example.CourseWork.export.FileUploadUtil;
import com.example.CourseWork.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;

@Controller
public class CategoryController {

    @Autowired
    private CategoryService service;

    @GetMapping("/categories")
    public String listAll(Model model){
       List<Category> listCategories=service.listAll();

       model.addAttribute("listCategories",listCategories);
       return "categories";
    }

    @GetMapping("/categories/new")
    public String newCategory(Model model){

        List<Category> listCategories = service.listCategoriesUsedInForm();

        model.addAttribute("category", new Category());
        model.addAttribute("pageTitle", "Создание новой категории");
        model.addAttribute("listCategories",listCategories);
        return"category_form";
    }
    @PostMapping("/categories/save")
    public String saveCategory(Category category, @RequestParam("fileImage") MultipartFile multipartFile,
                               RedirectAttributes redirectAttributes) throws IOException {
        if(!multipartFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
            category.setImage(fileName);

            Category savedCategory = service.save(category);

            String uploadDir = "category-images/" + savedCategory.getId();
            FileUploadUtil.cleanDir(uploadDir);
            FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
        }else {
            service.save(category);
        }
        redirectAttributes.addFlashAttribute("massage","Категория сохранена успешно");
        return "redirect:/categories";
    }

    @GetMapping("/categories/edit/{id}")
    public String editCategory(@PathVariable(name	= "id") Long id, Model model,
                               RedirectAttributes ra) {
        try {
            Category category = service.get(id);
            List<Category> listCategories = service.listCategoriesUsedInForm();
            model.addAttribute("category", category);
            model.addAttribute("listCategories", listCategories);
            model.addAttribute("pageTitle", "Редактирование категории (ID: " + id + ")");
            return "category_form";
        } catch (CategoryNotFoundException ex) {
            ra.addFlashAttribute("message", ex.getMessage());
            return "redirect:/categories";
        }
    }

    @GetMapping("/categories/delete/{id}")
    public String deleteUser(@PathVariable(name="id") Long id,RedirectAttributes redirectAttributes,Model model){
        try{
            service.delete(id);
            String categoryDir = "category-images/" + id;
            FileUploadUtil.removeDir(categoryDir);

            redirectAttributes.addFlashAttribute("massage","Категория была успешно удалёна");

        }catch(UserNotFoundException ex){
            redirectAttributes.addFlashAttribute("massage",ex.getMessage());
        }

        return "redirect:/categories";
    }

    @GetMapping("/categories/category_status/{id}")
    public String change_status(@PathVariable(name="id") Long id,RedirectAttributes redirectAttributes,Model model){
        try{
            service.changeStatus(id);
            redirectAttributes.addFlashAttribute("massage","Статус изменён успешно");

        }catch(UserNotFoundException ex){
            redirectAttributes.addFlashAttribute("massage",ex.getMessage());
        }

        return "redirect:/categories";
    }
}
