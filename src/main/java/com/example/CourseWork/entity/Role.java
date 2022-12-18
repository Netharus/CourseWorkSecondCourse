package com.example.CourseWork.entity;

import jakarta.persistence.*;
import lombok.*;
import nonapi.io.github.classgraph.json.Id;

@Entity
@Table(name="roles")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 40,nullable = false,unique = true)
    private String name;
    @Column(length = 140,nullable = false)
    private String description;


    public void setId(Long id) {
        this.id = id;
    }

    @jakarta.persistence.Id
    public Long getId() {
        return id;
    }

    @Override
    public String toString(){
        return this.name;
    }
}
