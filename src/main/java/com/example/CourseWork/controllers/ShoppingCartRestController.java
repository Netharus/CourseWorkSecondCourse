package com.example.CourseWork.controllers;

import com.example.CourseWork.entity.User;
import com.example.CourseWork.exceptions.ShopingCartException;
import com.example.CourseWork.exceptions.UserNotFoundException;
import com.example.CourseWork.service.ShoppingCartService;
import com.example.CourseWork.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ShoppingCartRestController {
    @Autowired
    private ShoppingCartService cartService;

    @Autowired
    private UserService userService;

    @PostMapping("/cart/add/{productId}/{quantity}")
    public String addProductToCart(@PathVariable("productId") Long productId,
    @PathVariable("quantity") Integer quantity,@AuthenticationPrincipal UserDetails loggedUser) {
        try{
            User user=getAuthenticatedCustomer(loggedUser);
            Integer updatedQuantity= cartService.addProduct(productId,quantity,user);
            return updatedQuantity+" товар(-а,-ов) этого типа было добавлено к вам в корзину";
        }catch(UserNotFoundException ex){
            return "Вы сперва должны войти в аккаунт прежде чем добавить товар в корзину";
        } catch (ShopingCartException e) {
            return e.getMessage();
        }
    }


    public User getAuthenticatedCustomer(@AuthenticationPrincipal UserDetails loggedUser)throws UserNotFoundException{
        String email = loggedUser.getUsername();
        if(email==null){
            throw new UserNotFoundException("");
        }
        return userService.getByEmail(email);
    }
    @PostMapping("/cart/update/{productId}/{quantity}")
    public String updateProductQuantity(@PathVariable("productId") Long productId,
                                   @PathVariable("quantity") Integer quantity,@AuthenticationPrincipal UserDetails loggedUser) {
        try{
            User user=getAuthenticatedCustomer(loggedUser);
            float subtotal=cartService.updateQuantity(productId,quantity,user);

            return String.valueOf(subtotal);
        }catch(UserNotFoundException ex){
            return "Вы сперва должны войти в аккаунт прежде чем изменять количество товаров в корзине";
        }
    }

    @DeleteMapping("/cart/remove/{productId}")
    public String removeProduct(@PathVariable("productId") Long productId,@AuthenticationPrincipal UserDetails loggedUser ){
        User user= null;
        try {
            user = getAuthenticatedCustomer(loggedUser);
            cartService.removeProduct(productId,user);
            return "Продукт был удалён из вашей корзины";
        } catch (UserNotFoundException e) {
           return "Вы сперва должны войти в аккаунт прежде чем удалять товары из корзины";
        }

    }
}
