package com.example.CourseWork.controllers;

import com.example.CourseWork.Info.CategoryPageInfo;
import com.example.CourseWork.entity.Category;
import com.example.CourseWork.entity.Product;
import com.example.CourseWork.exceptions.CategoryNotFoundException;
import com.example.CourseWork.exceptions.UserNotFoundException;
import com.example.CourseWork.export.FileUploadUtil;
import com.example.CourseWork.service.CategoryService;
import com.example.CourseWork.service.ProductService;
import com.example.CourseWork.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
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
    @Autowired
    private ProductService productService;

    @GetMapping("/categories")
    public String listFirstPage(@Param("sortDir")String sortDir, Model model){
        return listByPage(1,model,"asc",null);
    }

    @GetMapping("/categories/page/{pageNum}")
    public String listByPage(@PathVariable(name="pageNum") int pageNum, Model model,
                              @Param("sortDir") String sortDir,
                             @Param("keyword") String keyword){
        if (sortDir==null||sortDir.isEmpty()){
            sortDir="asc";
        }
        CategoryPageInfo pageInfo=new CategoryPageInfo();
        List<Category> listCategories=service.listByPage(pageInfo,pageNum,sortDir,keyword);
        String reverseSortDir=sortDir.equals("asc")?"desc":"asc";

        Long startCount= Long.valueOf((pageNum-1)* service.ROOT_CATEGORIES_PER_PAGE+1);
        Long endCount=startCount+service.ROOT_CATEGORIES_PER_PAGE-1;
        if(endCount>pageInfo.getTotalElements()){
            endCount=pageInfo.getTotalElements();
        }
        model.addAttribute("productService",productService);
        model.addAttribute("keyword", keyword);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("startCount",startCount);
        model.addAttribute("endCount",endCount);
        model.addAttribute("sortField", "name");
        model.addAttribute("currentPage",pageNum);
        model.addAttribute("totalPages",pageInfo.getTotalPages());
        model.addAttribute("totalItems",pageInfo.getTotalElements());
        model.addAttribute("reverseSortDir",reverseSortDir);
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
            ra.addFlashAttribute("massage","Категория была успешно изменена");
            return "category_form";
        } catch (CategoryNotFoundException ex) {
            ra.addFlashAttribute("message", ex.getMessage());
            return "redirect:/categories";
        }
    }

    @GetMapping("/categories/delete/{id}")
    public String deleteCategory(@PathVariable(name="id") Long id,RedirectAttributes redirectAttributes,Model model){
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
