package com.example.CourseWork.entity;

import jakarta.persistence.*;
import lombok.*;
import nonapi.io.github.classgraph.json.Id;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
//@ToString
@Table(name="categories")
public class Category {
    @jakarta.persistence.Id
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length=128,nullable = false,unique = true)
    private String name;
    @Column(length=64,nullable = false,unique = true)
    private String alias;
    @Column(length=128,nullable = false)
    private String image;
    private boolean enabled;

    public Category(String name) {
        this.name = name;
        this.alias = name;
        this.image = "default.png";
    }

    public static Category copyIdAndName(Category category) {
        Category newCategory= new Category();
        newCategory.setId(category.getId());
        newCategory.setName(category.getName());
        return newCategory;
    }

    public static Category copyIdAndName(Long id,String name) {
        Category newCategory= new Category();
        newCategory.setId(id);
        newCategory.setName(name);
        return newCategory;
    }


    public static Category copyFull(Category category) {
        Category copyCategory = new Category();
        copyCategory.setId(category.getId());
        copyCategory.setName(category.getName());
        copyCategory.setImage(category.getImage());
        copyCategory.setAlias(category.getAlias());
        copyCategory.setEnabled(category.isEnabled());
        copyCategory.setHasChildren(category.getChildren().size()>0);

        return copyCategory;
    }

    public Category(Long id, String name, String alias) {
        super();
        this.id = id;
        this.name = name;
        this.alias = alias;
    }

    public static Category copyFull(Category category, String name){
        Category copyCategory = Category.copyFull(category);
        copyCategory.setName(name);

        return copyCategory;
    }

    @OneToOne
    @JoinColumn(name="parent_id")
    private Category parent;

    @OneToMany(mappedBy = "parent")
    private Set<Category> children=new HashSet<>();

    @Transient
    public String getImagePath(){
        if (this.id == null) return "/images/image-thumbnail.png";
        return "/category-images/" + this.id + "/" + this.image;
    }

    @Override
    public String toString() {
        return "Category{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", alias='" + alias + '\'' +
                ", image='" + image + '\'' +
                ", enabled=" + enabled +
                ", parent=" +
                '}';
    }

    @Transient
    private boolean hasChildren;
}
