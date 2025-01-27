package com.example.CourseWork.repository;

import com.example.CourseWork.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<User,Long>, PagingAndSortingRepository<User,Long> {
    @Query("SELECT u FROM User u WHERE u.email= :email")
    public User getUserByEmail(@Param("email")String email);

    @Query("SELECT u FROM User u WHERE CONCAT(u.id, ' ', u.email, ' ', u.firstName,' ',"
            + " u. lastName) LIKE %?1%")
    public Page<User> findAll(String keyword, Pageable pageable);
    public Long countById(Long id);


    @Query("UPDATE User u SET u.enabled=true WHERE u.id=?1")
    @Modifying
    public void enabled(Long id);
}
