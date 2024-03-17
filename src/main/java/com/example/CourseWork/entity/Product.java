package com.example.CourseWork.entity;

import jakarta.persistence.*;
import lombok.*;
import nonapi.io.github.classgraph.json.Id;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Table(name="products")
public class Product {
    @jakarta.persistence.Id
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length=256,nullable = false,unique = true)
    private String name;
    @Column(length=256,nullable = false,unique = true)
    private String alias;
    @Column(length=512,nullable = false,name="short_description")
    private String shortDescription;
    @Column(length=4096,nullable = false,name="full_description")
    private String fullDescription;

    @Column(name="created_time")
    private Date createdTime;
    @Column(name="updated_time")
    private Date updatedTime;

    private boolean enabled;

    @Column(name="in_stock")
    private boolean inStock;

    private float cost;

    private float price;

    @Column(name="main_image",nullable = false)
    private String mainImage;
    @Column(name="discount_precent")
    private float discountPercent;

    @ManyToOne
    @JoinColumn(name= "category_id")
    private Category category;

    @OneToMany(mappedBy = "product",cascade = CascadeType.ALL)
    private Set<ProductImage> images=new HashSet<>();

    public void addExtraImage(String imageName){
        this.images.add(new ProductImage(imageName,this));
    }
    @Transient
    public String getMainImagePath(){
        if(id==null||mainImage==null)return "/images/image-thumbnail.png";
        return "/product-images/" + this.id + "/" + this.mainImage;
    }
    @Transient
    public float getDiscountPrice(){
        if(discountPercent>0){
            return price*((100-discountPercent)/100);
        }
        return this.price;
    }

    public Product(Long id) {
        this.id = id;
    }
}
