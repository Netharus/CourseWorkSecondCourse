package com.example.CourseWork.repository;

import com.example.CourseWork.entity.CartItem;
import com.example.CourseWork.entity.Product;
import com.example.CourseWork.entity.User;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartItemRepository extends CrudRepository<CartItem,Long> {

    public List<CartItem> findByUser(User user);

    public CartItem findByUserAndProduct(User user, Product product);

    @Modifying
    @Query("UPDATE CartItem c set c.quantity=?1 WHERE c.user.id=?2 AND c.product.id=?3")
    public void updateQuantity(Integer quantity,Long userId,Long productId );

    @Modifying
    @Query("DELETE FROM CartItem c WHERE c.user.id=?1 AND c.product.id=?2")
    public int deleteByUserAndProduct(Long userId,Long productId);
}
