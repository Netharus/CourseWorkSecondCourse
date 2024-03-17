package com.example.CourseWork.entity;

import jakarta.persistence.*;
import lombok.*;
import nonapi.io.github.classgraph.json.Id;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Table(name="cart_items")
public class CartItem {
    @jakarta.persistence.Id
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;
    @ManyToOne
    @JoinColumn(name="product_id")
    private Product product;
    private int quantity;
    @Transient
    public float getSubtotal(){
        return product.getDiscountPrice() * quantity;
    }
}
