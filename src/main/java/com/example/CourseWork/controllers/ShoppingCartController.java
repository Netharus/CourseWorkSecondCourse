package com.example.CourseWork.controllers;

import com.example.CourseWork.entity.CartItem;
import com.example.CourseWork.entity.User;
import com.example.CourseWork.exceptions.UserNotFoundException;
import com.example.CourseWork.service.ShoppingCartService;
import com.example.CourseWork.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private UserService userService;

    @GetMapping("/cart")
    public String viewCart(Model model,@AuthenticationPrincipal UserDetails loggedUser){
         List<CartItem> cartItems= shoppingCartService.listCartItems(getAuthenticatedCustomer(loggedUser));
         float estimatedTotal=0;
         for (CartItem item: cartItems){
             estimatedTotal+=item.getProduct().getDiscountPrice()*item.getQuantity();
         }
         model.addAttribute("cartItems",cartItems);
        model.addAttribute("estimatedTotal",estimatedTotal);
        return "shopping_cart";
    }

    public User getAuthenticatedCustomer(@AuthenticationPrincipal UserDetails loggedUser) {
        String email = loggedUser.getUsername();
        return userService.getByEmail(email);
    }
}
