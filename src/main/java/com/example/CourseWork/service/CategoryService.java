package com.example.CourseWork.service;

import com.example.CourseWork.entity.Category;
import com.example.CourseWork.entity.User;
import com.example.CourseWork.exceptions.CategoryNotFoundException;
import com.example.CourseWork.exceptions.UserNotFoundException;
import com.example.CourseWork.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

@Service
public class CategoryService {
    public static final int CATEGORIES_PER_PAGE = 10;
    @Autowired
    private CategoryRepository repo;

    public Page<Category> listByPage(int pageNum, String sortField, String sortDir, String keyword) {
        Sort sort = Sort.by(sortField);
        sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();

        Pageable pageable = PageRequest.of(pageNum - 1, CATEGORIES_PER_PAGE, sort);

        if (keyword != null) {
            //return repo.findAll(keyword,pageable);
        }
        return repo.findAll(pageable);
    }

    public List<Category> listAll() {
        List<Category> rootCategories =repo.findRootCategories();
        return listHierarchicalCategories(rootCategories);
    }

    private List<Category> listHierarchicalCategories(List<Category> rootCategories){
        List<Category> hierarchicalCategories=new ArrayList<>();

        for(Category rootCategory:rootCategories){
            hierarchicalCategories.add(Category.copyFull(rootCategory));

            Set<Category> children = rootCategory.getChildren();
            for(Category subCategory: children){
                String name ="--"+	subCategory.getName();
                hierarchicalCategories.add(Category.copyFull(subCategory,name));

                listSubHierarchicalCategories(hierarchicalCategories,subCategory,1);
            }
        }
        return hierarchicalCategories;
    }

    private void listSubHierarchicalCategories(List<Category> hierarchicalCategories,
                                               Category parent, int subLevel) {
        Set<Category> children = parent.getChildren();
        int newSubLevel = subLevel + 1;

        for (Category subCategory : children) {
            String name = "";
            for (int i = 0; i < newSubLevel; i++) {
                name += "--";
            }
            name += subCategory.getName();
            hierarchicalCategories.add(Category.copyFull(subCategory, name));
            listSubHierarchicalCategories(hierarchicalCategories, subCategory, newSubLevel);
        }
    }
    public Category save(Category category) {
            return repo.save(category);
    }

    public Category get(Long id) throws CategoryNotFoundException {
        try {
            return repo.findById(id).get();
        } catch (NoSuchElementException ex) {
            throw new CategoryNotFoundException("Could not find any category with ID " + id);
        }
    }
    public List<Category> listCategoriesUsedInForm() {
        List<Category> categoriesUsedInForm = new ArrayList<>();

        Iterable<Category> categoriesInDB = repo.findAll();

        for (Category category : categoriesInDB) {
            if (category.getParent() == null) {
                categoriesUsedInForm.add(Category.copyIdAndName(category));

                Set<Category> children = category.getChildren();

                for (Category subCategory : children) {
                    String name ="--"+	subCategory.getName();
                    categoriesUsedInForm.add(Category.copyIdAndName(subCategory.getId(),name));

                    listSubCategoriesUsedInForm(categoriesUsedInForm,subCategory, 1);
                }
            }
        }
        return categoriesUsedInForm;
    }


    private void listSubCategoriesUsedInForm(List<Category> categoriesUsedInForm,
                                             Category parent, int subLevel) {
        int newSubLevel = subLevel + 1;
        Set<Category> children = parent.getChildren();

        for (Category subCategory : children) {
            String name="";
            for (int i = 0; i < newSubLevel; i++) {
                name+="--";
            }
            name += subCategory.getName();
            categoriesUsedInForm.add(Category.copyIdAndName(subCategory.getId(),name));
            listSubCategoriesUsedInForm(categoriesUsedInForm,subCategory, newSubLevel);
        }
    }

    public void delete(Long id) throws UserNotFoundException {
        Long countById=repo.countById(id);
        if(countById==null||countById==0){
            throw new UserNotFoundException("Не может найти категорию с таким id" + id);
        }
        repo.deleteById(id);
    }
    public String checkUnique(Long id, String name, String alias) {
        boolean isCreatingNew = (id == null || id == 0);
        Category categoryByName = repo.findByName(name);
        if (isCreatingNew) {
            if (categoryByName != null){
                return "DuplicateName";
            }else{
                Category categoryByAlias=repo.findByAlias(alias);
                if(categoryByAlias!=null){
                    return "DuplicateAlias";
                }
            }
        }else{
            if(categoryByName != null && categoryByName.getId()!= id){
                return "DuplicateName";
            }
            Category categoryByAlias=repo.findByAlias(alias);
            if(categoryByAlias !=null && categoryByAlias.getId()!=id){
                return "DuplicateAlias";
            }
        }

        return "OK";
    }
    public void changeStatus(Long id) throws UserNotFoundException {
        Long countById= repo.countById(id);
        if(countById==null||countById==0){
            throw new UserNotFoundException("Не может найти категорию с таким ID" + id);
        }
        Category existingCategory=repo.findById(id).get();
        if (existingCategory.isEnabled()) {
            existingCategory.setEnabled(false);
        } else {
            existingCategory.setEnabled(true);
        }
        repo.save(existingCategory);
    }
}