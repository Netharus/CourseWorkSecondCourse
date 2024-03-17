package com.example.CourseWork.repository;

import com.example.CourseWork.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends CrudRepository<Product,Long>, PagingAndSortingRepository<Product,Long> {
    public Product findByName(String name);

    @Query("SELECT p FROM Product p WHERE p.name LIKE %?1%"
            + "OR p.shortDescription LIKE %?1% "
            + "OR p.fullDescription LIKE %?1% "
            + "OR p.category.name LIKE %?1%")
    public Page<Product> findAll(String keyword, Pageable pageable);
    Long countById(Long id);

    Product findByAlias(String alias);
    @Query("SELECT p FROM Product p WHERE p.enabled =true "+" AND (p.category.id = ?1 OR p.category.allParentIDs LIKE %?2%)" +" ORDER BY p.name ASC ")
    public Page<Product> listByCategory(Long categoryId,String categoryIDMatch,Pageable pageable);

    @Query(value = "SELECT * FROM products WHERE enabled = true AND "
            + "MATCH(name, short_description, full_description) AGAINST (?1)",
            nativeQuery = true)
    public Page<Product> search(String keyword, Pageable pageable);

}
