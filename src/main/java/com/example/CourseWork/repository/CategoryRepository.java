package com.example.CourseWork.repository;


import com.example.CourseWork.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Locale;

@Repository
public interface CategoryRepository extends  CrudRepository<Category,Long>,PagingAndSortingRepository<Category,Long> {

    @Query("SELECT с FROM Category с WHERE с.parent.id is NULL")
    public List<Locale.Category> findRootCategories(Sort sort);

    @Query("SELECT с FROM Category с WHERE с.parent.id is NULL")
    public Page<Category> findRootCategories(Pageable pageable);

    @Query("SELECT c FROM Category c WHERE c.name LIKE %?1%")
    public Page<Category> search(String keyword, Pageable pageable);
    public Long countById(Long id);


    public Category findByName(String name);
    public Category findByAlias(String name);

    @Query("SELECT c FROM Category c WHERE c.enabled =true ORDER BY c.name ASC ")
    public List<Category> findAllEnabled();

    @Query("SELECT c FROM Category c WHERE c.enabled = true AND c.alias = ?1")
    public Category findByAliasEnabled(String alias);
}
