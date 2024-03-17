package com.example.CourseWork.service;

import com.example.CourseWork.entity.CartItem;
import com.example.CourseWork.entity.Product;
import com.example.CourseWork.entity.User;
import com.example.CourseWork.exceptions.ShopingCartException;
import com.example.CourseWork.repository.CartItemRepository;
import com.example.CourseWork.repository.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class ShoppingCartService {
    @Autowired
    private CartItemRepository repo;
    @Autowired
    private ProductRepository productRepo;

    public Integer addProduct(Long productId, Integer quantity, User user) throws ShopingCartException {
        Integer updatedQuantity=quantity;
        Product product=new Product(productId);

        CartItem cartItem=repo.findByUserAndProduct(user,product);
        if(cartItem!=null){
            updatedQuantity=cartItem.getQuantity()+quantity;
            if(updatedQuantity>10){
                throw new ShopingCartException("Вы не можете добавить "+ quantity+" товаров, вы достигли максимального количества товаров в корзине. На данный момент их "+ cartItem.getQuantity()+". Максимум 10 товаров");
            }else{
                cartItem.setQuantity(updatedQuantity);
            }
        }else{
            cartItem=new CartItem();
            cartItem.setUser(user);
            cartItem.setProduct(product);
        }
        cartItem.setQuantity(updatedQuantity);
        repo.save(cartItem);
        return updatedQuantity;

    }
    public List<CartItem> listCartItems(User user){
        return repo.findByUser(user);
    }
    public float updateQuantity(Long productId, Integer quantity, User user) {
        repo.updateQuantity(quantity, user.getId(), productId);
        Product product = productRepo.findById(productId).get();
        float subtotal = product.getDiscountPrice() * quantity;
        return subtotal;
    }
    public void removeProduct(Long productId,User user){
        repo.deleteByUserAndProduct(user.getId(),productId);
    }
}
