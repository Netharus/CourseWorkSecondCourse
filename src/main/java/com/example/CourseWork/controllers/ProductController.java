package com.example.CourseWork.controllers;

import com.example.CourseWork.entity.Category;
import com.example.CourseWork.entity.Product;
import com.example.CourseWork.exceptions.CategoryNotFoundException;
import com.example.CourseWork.exceptions.ProductNotFoundException;
import com.example.CourseWork.exceptions.UserNotFoundException;
import com.example.CourseWork.export.FileUploadUtil;
import com.example.CourseWork.service.CategoryService;
import com.example.CourseWork.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;

@Controller
public class ProductController
{
    @Autowired
    private ProductService productService;


    @Autowired
    private CategoryService categoryService;
    @GetMapping("/products")
    public String listFirstPage(Model model){
        return listByPage(1,model,"name","asc",null);
    }

    @GetMapping("/products/new")
    public String newProduct(Model model){
        List<Product> listProducts=productService.listAll();
        List<Category> listCategories=categoryService.listAll();

        Product product=new Product();
        product.setEnabled(true);
        product.setInStock(true);

        model.addAttribute("pageTitle", "Создания нового продукта");
        model.addAttribute("product",product);
        model.addAttribute("listCategories",listCategories);
        return "product_form";
    }

    @PostMapping( "/products/save")
    public String saveProduct(Product product, RedirectAttributes redirectAttributes, @RequestParam("fileImage") MultipartFile multipartFile) throws IOException, ProductNotFoundException {
        if(!multipartFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
            product.setMainImage(fileName);

            Product savedProduct = productService.save(product);
            String uploadDir = "product-images/" + savedProduct.getId();

            FileUploadUtil.cleanDir(uploadDir);
            FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
        }else {
            productService.save(product);
        }
        redirectAttributes.addFlashAttribute("massage","Продукт сохранен успешно");
        return "redirect:/products";
    }
    @GetMapping("/products/product_status/{id}")
    public String change_status(@PathVariable(name="id") Long id, RedirectAttributes redirectAttributes){
        try{
            productService.changeStatus(id);
            redirectAttributes.addFlashAttribute("massage","Статус изменён успешно");

        }catch(UserNotFoundException ex){
            redirectAttributes.addFlashAttribute("massage",ex.getMessage());
        }

        return "redirect:/products";
    }

    @GetMapping("/products/delete/{id}")
    public String deleteProduct(@PathVariable(name="id") Long id,RedirectAttributes redirectAttributes){
        try{
            productService.delete(id);
            String categoryDir = "product-images/" + id;
            FileUploadUtil.removeDir(categoryDir);
            redirectAttributes.addFlashAttribute("massage","Товар была успешно удалёна");
        }catch(ProductNotFoundException ex){
            redirectAttributes.addFlashAttribute("massage",ex.getMessage());
        }

        return "redirect:/products";
    }
    @GetMapping("/products/edit/{id}")
    public String editProduct(@PathVariable(name	= "id") Long id, Model model,
                               RedirectAttributes ra) {
        try {
            Product product = productService.get(id);
            List<Category> listCategories = categoryService.listAll();
            model.addAttribute("product", product);
            model.addAttribute("listCategories", listCategories);
            model.addAttribute("pageTitle", "Редактирование категории (ID: " + id + ")");
            return "product_form";
        } catch (ProductNotFoundException ex) {
            ra.addFlashAttribute("message", ex.getMessage());
            return "redirect:/products";
        }
    }

    @GetMapping("/products/details/{id}")
    public String viewProductDetails(@PathVariable(name	= "id") Long id, Model model,
                               RedirectAttributes ra) {
        try {
            Product product = productService.get(id);
            model.addAttribute("product", product);
            return "product_details_modal";
        } catch (ProductNotFoundException ex) {
            ra.addFlashAttribute("message", ex.getMessage());
            return "redirect:/products";
        }
    }

    @GetMapping("/products/page/{pageNum}")
    public String listByPage(@PathVariable(name="pageNum") int pageNum, Model model,
                             @Param("sortField") String sortField, @Param("sortDir") String sortDir, @Param("keyword") String keyword){
        Page<Product> page =productService.listByPage(pageNum,sortField,sortDir,keyword);
        List<Product> listProducts=page.getContent();
        Integer currentPage=pageNum;
        Long startCount= Long.valueOf((pageNum-1)* productService.PRODUCTS_PER_PAGE+1);
        Long endCount=startCount+productService.PRODUCTS_PER_PAGE-1;
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
        model.addAttribute("listProducts",listProducts);
        model.addAttribute("sortField",sortField);
        model.addAttribute("sortDir",sortDir);
        model.addAttribute("reverseSortDir",reverseSortDir);
        return "products";
    }

    @GetMapping("/c/{category_alias}")
    public String viewCategoryFirstPage(@PathVariable("category_alias") String alias,Model model) {
        return viewCategoryByPage(alias,1,model);
    }

    @GetMapping("/c/{category_alias}/{pageNum}")
    public String viewCategoryByPage(@PathVariable("category_alias") String alias,@PathVariable("pageNum")int pageNum,Model model) {
       try {
           Category category = categoryService.getCategory(alias);
           if (category == null) {
               return "error";
           }

           List<Category> listCategoryParent = categoryService.getCategoryParents(category);
           Page<Product> pageProducts = productService.listByCategory(pageNum, category.getId());
           List<Product> listProducts = pageProducts.getContent();
           Long startCount = Long.valueOf((pageNum - 1) * productService.PRODUCTS_PER_PAGE + 1);
           Long endCount = startCount + productService.PRODUCTS_PER_PAGE - 1;
           if (endCount > pageProducts.getTotalElements()) {
               endCount = pageProducts.getTotalElements();
           }

           model.addAttribute("category", category);
           model.addAttribute("currentPage", pageNum);
           model.addAttribute("startCount", startCount);
           model.addAttribute("endCount", endCount);
           model.addAttribute("totalItems", pageProducts.getTotalElements());
           model.addAttribute("totalPages", pageProducts.getTotalPages());
           model.addAttribute("pageTitle", category.getName());
           model.addAttribute("listCategoryParent", listCategoryParent);
           model.addAttribute("listProducts", listProducts);
           return "products_by_category";
       }catch(CategoryNotFoundException ex){
           return "error";
       }
    }

    @GetMapping("/p/{product_alias}")
    public String viewProductDetail(@PathVariable("product_alias") String alias,Model model){
        try{
            Product product=productService.getProduct(alias);
            List<Category> listCategoryParent= categoryService.getCategoryParents(product.getCategory());
            model.addAttribute("listCategoryParent", listCategoryParent);
            model.addAttribute("product",product);
            model.addAttribute("pageTitle",product.getName());

            return "product_detail";
        }catch(ProductNotFoundException ex){
            return "error";
        }
    }

    @GetMapping("/userSearch")
    public String searchFirstPage(@Param("keyword") String keyword,Model model){
        return searchByPage(keyword,model,1);
    }
    @GetMapping("/userSearch/page/{pageNum}")
    public String searchByPage(@Param("keyword") String keyword, Model model, @PathVariable("pageNum") int pageNum){
        Page<Product> pageProducts=productService.search(keyword,pageNum);
        List<Product> listProducts =pageProducts.getContent();

        Long startCount = Long.valueOf((pageNum - 1) * productService.PRODUCTS_PER_PAGE + 1);
        Long endCount = startCount + productService.PRODUCTS_PER_PAGE - 1;
        if (endCount > pageProducts.getTotalElements()) {
            endCount = pageProducts.getTotalElements();
        }
        model.addAttribute("totalItems", pageProducts.getTotalElements());
        model.addAttribute("currentPage", pageNum);
        model.addAttribute("startCount", startCount);
        model.addAttribute("endCount", endCount);
        model.addAttribute("keyword",keyword);
        model.addAttribute("listProducts",listProducts);
        model.addAttribute("pageTitle",keyword);
        return "search_result";
    }
}
