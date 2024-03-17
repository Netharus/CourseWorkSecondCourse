package com.example.CourseWork.service;

import com.example.CourseWork.Info.CategoryPageInfo;
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

import java.util.*;

@Service
public class CategoryService {
    public static final int ROOT_CATEGORIES_PER_PAGE = 4;
    @Autowired
    private CategoryRepository repo;

    public List<Category> listByPage(CategoryPageInfo pageInfo, int pageNum, String sortDir,
                                     String keyword) {
        Sort sort = Sort.by("name");

        sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();

        Pageable pageable = PageRequest.of(pageNum - 1, ROOT_CATEGORIES_PER_PAGE, sort);

        Page<Category> pageCategories=null;

        if (keyword != null && !keyword.isEmpty()) {
           pageCategories = repo.search(keyword, pageable);
        } else {
           pageCategories = repo.findRootCategories(pageable);
        }

        List<Category> rootCategories = pageCategories.getContent();

        pageInfo.setTotalElements(pageCategories.getTotalElements());
        pageInfo.setTotalPages(pageCategories.getTotalPages());

        if (keyword != null && !keyword.isEmpty()) {
            List<Category> searchResult=pageCategories.getContent();
            pageCategories = repo.search(keyword, pageable);
            for (Category category: searchResult){
                category.setHasChildren(category.getChildren().size()>0);
            }
            return searchResult;
        } else {
            return listHierarchicalCategories(rootCategories,sortDir);
        }


    }

    public List<Category> listAll() {
        return listCategoriesUsedInForm();
    }

    private List<Category> listHierarchicalCategories(List<Category> rootCategories,String sortDir){
        List<Category> hierarchicalCategories=new ArrayList<>();

        for(Category rootCategory:rootCategories){
            hierarchicalCategories.add(Category.copyFull(rootCategory));

            Set<Category> children = sortSubCategories(rootCategory.getChildren(),sortDir);
            for(Category subCategory: children){
                String name ="--"+	subCategory.getName();
                hierarchicalCategories.add(Category.copyFull(subCategory,name));

                listSubHierarchicalCategories(hierarchicalCategories,subCategory,1,sortDir);
            }
        }
        return hierarchicalCategories;
    }

    private void listSubHierarchicalCategories(List<Category> hierarchicalCategories,
                                               Category parent, int subLevel,String sortDir) {
        Set<Category> children = sortSubCategories(parent.getChildren(),sortDir);
        int newSubLevel = subLevel + 1;

        for (Category subCategory : children) {
            String name = "";
            for (int i = 0; i < newSubLevel; i++) {
                name += "--";
            }
            name += subCategory.getName();
            hierarchicalCategories.add(Category.copyFull(subCategory, name));
            listSubHierarchicalCategories(hierarchicalCategories, subCategory, newSubLevel,sortDir);
        }
    }
    public Category save(Category category) {
        if(category.getAlias()==null||category.getAlias().isEmpty()){
            String defaultAlias=category.getName().replaceAll(" ","_");
            category.setAlias(defaultAlias);
        }else{
            category.setAlias(category.getAlias().replaceAll(" ","_"));
        }
        Category parent = category.getParent();
        if (parent != null) {
            String allParentIds = parent.getAllParentIDs() == null ? "-" : parent.getAllParentIDs();
            allParentIds += String.valueOf(parent.getId()) + "-";
            category.setAllParentIDs(allParentIds);
        }
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

                Set<Category> children = sortSubCategories(category.getChildren());

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
        Set<Category> children = sortSubCategories(parent.getChildren());

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

    private SortedSet<Category> sortSubCategories(Set<Category> children){
        return sortSubCategories(children, "asc");
    }
    private SortedSet<Category> sortSubCategories(Set<Category> children,String sortDir) {
        SortedSet<Category> sortedChildren = new TreeSet<>(new Comparator<Category>() {
            @Override
            public int compare(Category cat1, Category cat2) {
                if(sortDir.equals("asc")){
                    return cat1.getName().compareTo(cat2.getName());
                }else{
                    return cat2.getName().compareTo(cat1.getName());
                }
            }
        });

        sortedChildren.addAll(children);
                return sortedChildren;
    }

    public List<Category> listNoChildrenCategories(){
        List<Category> listNoChildren=new ArrayList<>();
        List<Category> findAllEnabled=repo.findAllEnabled();

        findAllEnabled.forEach(category->{
            Set<Category> children=category.getChildren();
            if(children==null||children.size()==0){
                listNoChildren.add(category);
            }
        });

        return listNoChildren;
    }
    public Category getCategory(String alias) throws CategoryNotFoundException {
        Category category= repo.findByAliasEnabled(alias);
        if(category==null){
            throw new CategoryNotFoundException("Не можем найти категорию с таким псевданимом"+alias);
        }
        return category;
    }
    public List<Category> getCategoryParents(Category child) {
        List<Category> listParents = new ArrayList<>();
        Category parent = child.getParent();
        while (parent != null) {
            listParents.add(0, parent);
            parent = parent.getParent();
        }
        listParents.add(child);
        return listParents;
    }
}