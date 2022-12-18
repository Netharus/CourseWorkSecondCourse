package com.example.CourseWork.repository;


import com.example.CourseWork.entity.Category;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface CategoryRepository extends CrudRepository<Category,Long>,PagingAndSortingRepository<Category,Long> {

    @Query("SELECT с FROM Category с WHERE с.parent.id is NULL")
    public List<Category> findRootCategories();

    public Long countById(Long id);

    public Category findByName(String name);
    public Category findByAlias(String name);
}
